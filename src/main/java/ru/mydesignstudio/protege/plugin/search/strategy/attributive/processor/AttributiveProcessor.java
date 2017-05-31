package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.weight.calculator.AttributiveRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;

/**
 * Created by abarmin on 12.03.17.
 *
 * Процессор для поиска по атрибутам
 */
public class AttributiveProcessor extends SparqlProcessorSupport implements SearchProcessor<AttributiveProcessorParams> {
    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, AttributiveProcessorParams strategyParams) throws ApplicationException {
        return strategyParams.getSelectQuery();
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, AttributiveProcessorParams strategyParams) throws ApplicationException {
        final ResultSet resultSet = collect(selectQuery);
        return new WeighedResultSet(resultSet, getWeightCalculator(selectQuery, strategyParams));
    }

    private WeighedRowWeightCalculator getWeightCalculator(SelectQuery selectQuery, SearchProcessorParams processorParams) {
        return new AttributiveRowWeightCalculator(selectQuery, processorParams);
    }
}
