package ru.mydesignstudio.protege.plugin.search.service.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchStrategyRegistry {
    private final Collection<SearchStrategy> strategies = new HashSet<>();

    public void register(SearchStrategy strategy) {
        strategies.add(strategy);
    }

    public Collection<SearchStrategy> getStrategies() {
        return Collections.unmodifiableCollection(strategies);
    }
}
