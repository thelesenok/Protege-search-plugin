package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.support.SparqlProcessorSupport;

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
        return collect(selectQuery);
    }
}
