package ru.mydesignstudio.protege.plugin.search.api.search.params;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyParams;

/**
 * Created by abarmin on 12.03.17.
 */
public class LookupParam {
    private final SearchStrategy strategy;
    private final SearchStrategyParams strategyParams;

    public LookupParam(SearchStrategy strategy, SearchStrategyParams strategyParams) {
        this.strategy = strategy;
        this.strategyParams = strategyParams;
    }

    public SearchStrategy getStrategy() {
        return strategy;
    }

    public SearchStrategyParams getStrategyParams() {
        return strategyParams;
    }
}
