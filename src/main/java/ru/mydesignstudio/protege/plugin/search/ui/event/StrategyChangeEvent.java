package ru.mydesignstudio.protege.plugin.search.ui.event;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.SearchStrategy;

/**
 * Created by abarmin on 03.01.17.
 */
public class StrategyChangeEvent implements Event {
    private final SearchStrategy strategy;

    public StrategyChangeEvent(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public SearchStrategy getStrategy() {
        return strategy;
    }
}
