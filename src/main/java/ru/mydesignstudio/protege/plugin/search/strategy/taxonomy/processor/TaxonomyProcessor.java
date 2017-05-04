package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;

/**
 * Created by abarmin on 04.05.17.
 *
 * Искалка с учетом таксономии
 */
public class TaxonomyProcessor implements SearchProcessor<TaxonomyCollectorParams> {
    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, TaxonomyCollectorParams strategyParams) throws ApplicationException {
        return initialQuery;
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, TaxonomyCollectorParams strategyParams) throws ApplicationException {
        return initialResultSet;
    }
}
