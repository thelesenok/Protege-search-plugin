package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryConverter;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.weight.calculator.AttributiveRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by abarmin on 12.03.17.
 *
 * Процессор для поиска по атрибутам
 */
@Component
public class AttributiveProcessor extends SparqlProcessorSupport implements SearchProcessor<AttributiveProcessorParams> {
    @Inject
    public AttributiveProcessor(OWLService owlService, ExceptionWrapperService wrapperService, SparqlQueryConverter queryConverter) {
        super(owlService, wrapperService, queryConverter);
    }

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery,
                                    AttributiveProcessorParams strategyParams,
                                    Collection<? extends SearchProcessorParams> allParameters) throws ApplicationException {

        // We are not checking initial query here because initial query here stored in strategy params
        Validation.assertNotNull("Attributive query not provided", strategyParams.getSelectQuery());
        Validation.assertNotNull("Strategy parameters not provided", strategyParams);
        Validation.assertNotNull("Other strategies parameters not provided", allParameters);

        return strategyParams.getSelectQuery();
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet,
                             SelectQuery selectQuery,
                             AttributiveProcessorParams strategyParams) throws ApplicationException {
        /**
         * делаем выборку данных по атрибутам
         */
        final ResultSet dataResultSet = collect(selectQuery);
        /**
         * взвешиваем их
         */
        final WeighedResultSet resultSet = toWeightedResultSet(
                dataResultSet, getRowWeightCalculator(selectQuery, strategyParams)
        );
        /**
         * добавляем предыдущие данные
         */
        resultSet.addResultSet(initialResultSet, getRowWeightCalculator(selectQuery, strategyParams));
        /**
         * на выход
         */
        return resultSet;
    }

    /**
     * Get row weight calculator instance.
     * @param selectQuery select query
     * @param processorParams processor params
     * @return row weight calculator
     */
    private WeighedRowWeightCalculator getRowWeightCalculator(SelectQuery selectQuery, AttributiveProcessorParams processorParams) {
        return new AttributiveRowWeightCalculator(selectQuery, processorParams);
    }
}
