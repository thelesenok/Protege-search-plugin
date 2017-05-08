package ru.mydesignstudio.protege.plugin.search.service.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 08.05.17.
 */
public class SearchStrategyRegistryImpl implements SearchStrategyRegistry {
    private final Map<Class<? extends SearchStrategy>, SearchStrategy> strategies = new HashMap<>();

    public void register(SearchStrategy strategy) {
        strategies.put(
                strategy.getClass(),
                strategy
        );
    }

    public Collection<SearchStrategy> getStrategies() {
        return Collections.unmodifiableCollection(strategies.values());
    }

    @Override
    public <T extends SearchStrategy> T getStrategy(Class<T> strategyClass) {
        return (T) strategies.get(strategyClass);
    }
}
