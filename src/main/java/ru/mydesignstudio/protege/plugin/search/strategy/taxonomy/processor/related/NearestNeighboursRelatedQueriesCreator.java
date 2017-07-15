package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import static ru.mydesignstudio.protege.plugin.search.utils.OWLUtils.getClassesInHierarchy;

/**
 * Created by abarmin on 24.06.17.
 *
 * Компонент создания похожих запросов методом ближайших соседей
 */
@Component
public class NearestNeighboursRelatedQueriesCreator implements RelatedQueriesCreator {
    private final OWLService owlService;

    public NearestNeighboursRelatedQueriesCreator() {
        owlService = InjectionUtils.getInstance(OWLService.class);
    }

    @Override
    public Collection<SelectQuery> create(SelectQuery initialQuery, SearchProcessorParams searchProcessorParams) throws ApplicationException {
        final TaxonomyProcessorParams processorParams = (TaxonomyProcessorParams) searchProcessorParams;
        final Collection<SelectQuery> relatedQueries = new ArrayList<>();
        /**
         * получим класс, по которому ищем
         */
        final OWLClass defaultFromClass = initialQuery.getFrom().getOwlClass();
        final OWLDomainClass domainClass = new OWLDomainClass(defaultFromClass);
        /**
         * посмотрим путь до вершины иерархии
         */
        final Collection<OWLDomainClass> classesInHierarchy = OWLUtils.getClassesInHierarchy(domainClass);
        /**
         * получим все классы, которые имеют указанные общие вершины
         */
        final Collection<OWLDomainClass> sharedClasses = getSharedClasses(classesInHierarchy, processorParams.getProximity());
        if (sharedClasses.size() > 0) {
            /**
             * на каждый из найденных общих классов формируем свой запрос,
             * их будем выполнять в collect-е, объединять результаты и
             * смотреть степень близости
             */
            for (OWLDomainClass sharedClass : sharedClasses) {
                final SelectQuery relatedQuery = createRelatedQuery(sharedClass, initialQuery);
                /**
                 * проверим, что хоть одно свойство совпало чтобы
                 * не выводить все записи
                 */
                if (!initialQuery.getWhereParts().isEmpty() && !relatedQuery.getWhereParts().isEmpty()) {
                    relatedQueries.add(relatedQuery);
                }
            }
        }
        return relatedQueries;
    }

    /**
     * Все классы, которые имеют указанное число общих вершин
     * @param classesInHierarchy - из чего набираем общие вершины
     * @param proximity - сколько общих вершин надо
     * @throws ApplicationException
     * @return
     */
    private Collection<OWLDomainClass> getSharedClasses(Collection<OWLDomainClass> classesInHierarchy, int proximity) throws ApplicationException {
        if (proximity > classesInHierarchy.size()) {
            /**
             * если требуемое количество общих вершин больше, чем уровень,
             * на котором находится класс, то общих классов больше нет
             */
            return Collections.emptyList();
        }
        /**
         * отрежем остальные общие вершины
         */
        final Collection<OWLDomainClass> targetHierarchy = new LinkedList<>();
        int index = 0;
        for (OWLDomainClass domainClass : classesInHierarchy) {
            if (index < proximity) {
                targetHierarchy.add(domainClass);
            }
        }
        /**
         * пойдем самым тупым путем - будем собирать общие классы
         * путем получения пути для всех имеющихся классов
         */
        final Collection<OWLDomainClass> sharedClasses = new HashSet<>();
        final Collection<OWLClass> allClasses = owlService.getClasses();
        //
        for (OWLClass owlClass : allClasses) {
            final OWLDomainClass domainClass = new OWLDomainClass(owlClass);
            final Collection<OWLDomainClass> currentHierarchy = getClassesInHierarchy(domainClass);
            if (startsWith(currentHierarchy, targetHierarchy)) {
                sharedClasses.add(domainClass);
            }
        }
        //
        return Collections.unmodifiableCollection(sharedClasses);
    }

    /**
     * Создаем запрос для близкого класса на основе исходного запроса
     * @param sharedClass - для какого класса делаем запрос
     * @param initialQuery - запрос, на основе которого все это делается
     * @throws ApplicationException
     * @return
     */
    public SelectQuery createRelatedQuery(OWLDomainClass sharedClass, SelectQuery initialQuery) throws ApplicationException {
        final SelectQuery sharedQuery = new SelectQuery();
        sharedQuery.setFrom(new FromType(sharedClass.getOwlClass()));
        /**
         * пройдем по всем параметра запроса initialQuery, возьмем только те
         * свойства, которые есть у sharedClass, скопируем критерии с теми же
         * условиями
         */
        for (WherePart wherePart : initialQuery.getWhereParts()) {
            if (OWLUtils.hasProperty(sharedClass, wherePart.getProperty())) {
                final WherePart clonedPart = wherePart.clone();
                if (clonedPart.getOwlClass().equals(initialQuery.getFrom().getOwlClass())) {
                    clonedPart.setOwlClass(sharedClass.getOwlClass());
                }
                sharedQuery.addWherePart(clonedPart);
            }
        }
        return sharedQuery;
    }

    /**
     * Проверяет, начинается ли коллекция source с target
     * @param source - какую коллекцию проверяем
     * @param target - с чего должна начинаться
     * @return
     */
    private boolean startsWith(Collection<OWLDomainClass> source, Collection<OWLDomainClass> target) {
        if (target.size() > source.size()) {
            /**
             * Проверяемая иерархия короче целевой,
             * точно не совпадает
             */
            return false;
        }
        final Iterator<OWLDomainClass> sourceIterator = source.iterator();
        final Iterator<OWLDomainClass> targetIterator = target.iterator();
        while (targetIterator.hasNext()) {
            if (sourceIterator.hasNext()) {
                final OWLDomainClass sourceClass = sourceIterator.next();
                final OWLDomainClass targetClass = targetIterator.next();
                if (!OWLUtils.equals(sourceClass, targetClass)) {
                    return false;
                }
            }
        }
        return true;
    }
}
