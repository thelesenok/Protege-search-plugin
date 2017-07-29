package ru.mydesignstudio.protege.plugin.search.strategy.relational.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.weight.calculator.RelationalRowWeightCalculator;

/**
 * Created by abarmin on 04.05.17.
 *
 * "Искатель" с учетом связей
 */
public class RelationalProcessor implements SearchProcessor<RelationalProcessorParams> {
    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, RelationalProcessorParams strategyParams) throws ApplicationException {
        return initialQuery;
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, RelationalProcessorParams strategyParams) throws ApplicationException {
        /**
         * Wrapping source resultSet with new WeighedResultSet
         */
        return new WeighedResultSet(initialResultSet, getWeightCalculator(selectQuery, strategyParams));
    }

    private WeighedRowWeightCalculator getWeightCalculator(SelectQuery selectQuery, RelationalProcessorParams strategyParams) {
        return new RelationalRowWeightCalculator(selectQuery, strategyParams);
    }
}
