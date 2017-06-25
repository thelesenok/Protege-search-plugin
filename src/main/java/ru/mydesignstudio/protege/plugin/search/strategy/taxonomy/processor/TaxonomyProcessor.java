package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.EqualClassesRelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.NearestNeighboursRelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.weight.calculator.TaxonomyRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 04.05.17.
 *
 * Искалка с учетом таксономии
 */
public class TaxonomyProcessor extends SparqlProcessorSupport implements SearchProcessor<TaxonomyProcessorParams> {
    @Inject
    private OWLService owlService;
    @Inject
    private ExceptionWrapperService wrapperService;

    private final RelatedQueriesCreator nearestNeighboursCreator = new NearestNeighboursRelatedQueriesCreator();
    private final RelatedQueriesCreator equalClassesCreator = new EqualClassesRelatedQueriesCreator();

    private TaxonomyProcessorParams processorParams;
    private Collection<SelectQuery> relatedQueries = new ArrayList<>();

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, TaxonomyProcessorParams strategyParams) throws ApplicationException {
        /**
         * сохраним параметры процессора
         */
        processorParams = strategyParams;
        /**
         * на случай, если несколько раз ищем
         */
        relatedQueries = new ArrayList<>();
        /**
         * если включен метод ближайших соседей - ищем по нему
         */
        if (isNearestNeighboursMethodEnabled(strategyParams)) {
            /**
             * добавим запросы для метода ближайших соседей
             */
            relatedQueries.addAll(nearestNeighboursCreator.create(initialQuery.clone(), strategyParams));
        }
        /**
         * если включен метод эквивалентных классов - добавляем
         */
        if (isEqualClassesMethodEnabled(strategyParams)) {
            /**
             * добавим запросы для метода эквивалентных классов
             */
            relatedQueries.addAll(equalClassesCreator.create(initialQuery.clone(), strategyParams));
        }
        /**
         * далее возвращаем исходный запрос, он здесь не
         * модифицируется, а только сохраняется на всякий случай
         */
        return initialQuery;
    }

    /**
     * Включен ли метод эквивалентных классов
     * @param processorParams - параметры компонента
     * @return - признак включенности
     */
    private boolean isEqualClassesMethodEnabled(TaxonomyProcessorParams processorParams) {
        return processorParams.isEqualsClassesMethodEnabled();
    }

    /**
     * Включен ли метод ближайших соседей
     * @param processorParams - параметры компонента
     * @return - признак включенности
     */
    private boolean isNearestNeighboursMethodEnabled(TaxonomyProcessorParams processorParams) {
        return processorParams.isNearestNeighboursMethodEnabled();
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, TaxonomyProcessorParams strategyParams) throws ApplicationException {
        /**
         * выполним поиск по каждому из заготовленных запросов
         */
        final Collection<ResultSet> relatedData = new ArrayList<>();
        for (SelectQuery relatedQuery : relatedQueries) {
            final ResultSet relatedResultSet = collect(relatedQuery);
            relatedData.add(relatedResultSet);
        }
        /**
         * так как кретерии поиска из "похожих" запросов могут быть убраны, то нужно проверить результаты,
         * содержат ли они необходимые поля и соответствуют ли их значения параметрам из исходного запроса
         */
        for (ResultSet relatedSet : relatedData) {
            for (ResultSetRow row : relatedSet.getRows()) {
                if (!isValidRow(row, selectQuery)) {
                    relatedSet.removeRow(row);
                }
            }
        }
        /**
         * объединим результаты и вычислим близость
         */
        return mergeResultSets(initialResultSet, relatedData);
    }

    /**
     * Является ли запись подходящей под исходные условия поиска
     * @param row - строка для проверки
     * @param selectQuery - исходные условия отбора записей
     * @return - признак того, что запись подходит
     * @throws ApplicationException - если не удается получить запись
     */
    private boolean isValidRow(ResultSetRow row, SelectQuery selectQuery) throws ApplicationException {
        /**
         * пока сделаем проверку самым простым образом, что экземпляр записи из конкретной строки содержит
         * свойства, по которым мы пытались искать
         */
        final OWLIndividual individual = owlService.getIndividual(row.getObjectIRI());
        final Collection<OWLClass> classes = owlService.getIndividualClasses(individual);
        /**
         * в это месте надо придумать, как быть с условиями, которые объединены через OR, а не через AND
         */
        return CollectionUtils.every(selectQuery.getWhereParts(), new Specification<WherePart>() {
            @Override
            public boolean isSatisfied(WherePart wherePart) {
                final OWLProperty property = wherePart.getProperty();
                return CollectionUtils.some(classes, new Specification<OWLClass>() {
                    @Override
                    public boolean isSatisfied(OWLClass owlClass) {
                        final Collection<OWLDataProperty> dataProperties = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<OWLDataProperty>>() {
                            @Override
                            public Collection<OWLDataProperty> run() throws ApplicationException {
                                return owlService.getDataProperties(owlClass);
                            }
                        });
                        final Collection<OWLObjectProperty> objectProperties = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Collection<OWLObjectProperty>>() {
                            @Override
                            public Collection<OWLObjectProperty> run() throws ApplicationException {
                                return owlService.getObjectProperties(owlClass);
                            }
                        });
                        return CollectionUtils.some(dataProperties, new Specification<OWLDataProperty>() {
                            @Override
                            public boolean isSatisfied(OWLDataProperty dataProperty) {
                                return OWLUtils.equals(property, dataProperty);
                            }
                        }) || CollectionUtils.some(objectProperties, new Specification<OWLObjectProperty>() {
                            @Override
                            public boolean isSatisfied(OWLObjectProperty objectProperty) {
                                return OWLUtils.equals(property, objectProperty);
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Объединить все результаты
     * @param sourceData - результат выполнения исходного запроса
     * @param relatedData - результаты выполнения "близких" запросов
     * @throws ApplicationException - если вдруг что случилось
     * @return - результирующий набор данных
     */
    private ResultSet mergeResultSets(ResultSet sourceData, Collection<ResultSet> relatedData) throws ApplicationException {
        final WeighedResultSet resultSet = new WeighedResultSet(sourceData, getRowWeightCalculator());
        for (ResultSet relatedDatum : relatedData) {
            resultSet.addResultSet(relatedDatum);
        }
        return resultSet;
    }

    /**
     * Калькулятор для вычисления веса строки
     * @return - объект калькулятора
     * @throws ApplicationException
     */
    public WeighedRowWeightCalculator getRowWeightCalculator() throws ApplicationException {
        return new TaxonomyRowWeightCalculator(processorParams);
    }
}
