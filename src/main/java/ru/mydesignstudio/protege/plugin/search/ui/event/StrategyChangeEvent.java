package ru.mydesignstudio.protege.plugin.search.ui.event;

import ru.mydesignstudio.protege.plugin.search.api.Event;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;

/**
 * Created by abarmin on 03.01.17.
 */
public class StrategyChangeEvent implements Event {
    private final SearchStrategy strategy;
    private final boolean isSelected;

    public StrategyChangeEvent(SearchStrategy strategy, boolean isSelected) {
        this.strategy = strategy;
        this.isSelected = isSelected;
    }

    public SearchStrategy getStrategy() {
        return strategy;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
