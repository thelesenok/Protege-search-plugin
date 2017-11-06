package ru.mydesignstudio.protege.plugin.search.strategy.relational.processor;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryConverter;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.weight.calculator.RelationalRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by abarmin on 04.05.17.
 *
 * "Искатель" с учетом связей
 */
public class RelationalProcessor extends SparqlProcessorSupport implements SearchProcessor<RelationalProcessorParams> {
    /**
     * {@inheritDoc}
     */
    @Inject
    public RelationalProcessor(OWLService owlService,
                               ExceptionWrapperService wrapperService,
                               SparqlQueryConverter queryConverter) {
        super(owlService, wrapperService, queryConverter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery,
                                    RelationalProcessorParams strategyParams,
                                    Collection<? extends SearchProcessorParams> allParameters) throws ApplicationException {

        Validation.assertNotNull("Initial query not provided", initialQuery);
        Validation.assertNotNull("Strategy params not provided", strategyParams);
        Validation.assertNotNull("Other strategies parameters not provided", allParameters);

        return initialQuery;
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, RelationalProcessorParams strategyParams) throws ApplicationException {
        /**
         * Wrapping source resultSet with new WeighedResultSet
         */
        return toWeightedResultSet(initialResultSet, getWeightCalculator(selectQuery, strategyParams));
    }

    private WeighedRowWeightCalculator getWeightCalculator(SelectQuery selectQuery, RelationalProcessorParams strategyParams) {
        return new RelationalRowWeightCalculator(selectQuery, strategyParams);
    }
}
