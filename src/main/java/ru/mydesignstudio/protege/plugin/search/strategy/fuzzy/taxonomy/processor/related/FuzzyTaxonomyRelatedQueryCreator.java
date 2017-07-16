package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLClass;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.FuzzySimilarClass;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilder;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.EqualClassesRelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;

/**
 * Created by abarmin on 25.06.17.
 *
 * Создает связанные запросы на основе данные из аннотации
 */
@Component
public class FuzzyTaxonomyRelatedQueryCreator extends EqualClassesRelatedQueriesCreator implements RelatedQueriesCreator {
    private final FuzzyOWLService fuzzyOWLService;

    @Inject
    public FuzzyTaxonomyRelatedQueryCreator(OWLService owlService, OwlClassHierarchyBuilder hierarchyBuilder,
			FuzzyOWLService fuzzyOWLService) {
    	
		super(owlService, hierarchyBuilder);
		this.fuzzyOWLService = fuzzyOWLService;
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
