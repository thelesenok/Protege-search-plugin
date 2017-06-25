package ru.mydesignstudio.protege.plugin.search.strategy.relational.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;

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
        return initialResultSet;
    }
}
