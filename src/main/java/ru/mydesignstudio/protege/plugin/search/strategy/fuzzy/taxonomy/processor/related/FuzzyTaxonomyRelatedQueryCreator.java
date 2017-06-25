package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.FuzzySimilarClass;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.EqualClassesRelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 25.06.17.
 *
 * Создает связанные запросы на основе данные из аннотации
 */
public class FuzzyTaxonomyRelatedQueryCreator extends EqualClassesRelatedQueriesCreator implements RelatedQueriesCreator {
    private final FuzzyOWLService fuzzyOWLService;

    public FuzzyTaxonomyRelatedQueryCreator() {
        fuzzyOWLService = InjectionUtils.getInstance(FuzzyOWLService.class);
    }

    @Override
    public Collection<SelectQuery> create(SelectQuery initialQuery, SearchProcessorParams processorParams) throws ApplicationException {
        /**
         * получим класс, по которому исходно делался запрос
         */
        final OWLClass initialFromClass = initialQuery.getFrom().getOwlClass();
        final Collection<FuzzySimilarClass> similarClasses = fuzzyOWLService.getFuzzySimilarClasses(initialFromClass);
        //
        final Collection<SelectQuery> relatedQueries = new ArrayList<>();
        for (FuzzySimilarClass similarClass : similarClasses) {
            relatedQueries.add(createRelatedQuery(
                    similarClass,
                    initialQuery
            ));
        }
        return relatedQueries;
    }
}
