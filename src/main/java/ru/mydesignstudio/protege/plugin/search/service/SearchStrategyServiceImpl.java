package ru.mydesignstudio.protege.plugin.search.service;

import ru.mydesignstudio.protege.plugin.search.api.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.strategy.DefaultSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.SemanticalSearchStrategy;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchStrategyServiceImpl implements SearchStrategyService {
    @Inject
    private SearchStrategyRegistry registry;
    @Inject
    private DefaultSearchStrategy defaultSearchStrategy;
    @Inject
    private SemanticalSearchStrategy semanticalSearchStrategy;

    @PostConstruct
    public void init() {
        register(defaultSearchStrategy);
        register(semanticalSearchStrategy);
    }

    @Override
    public void register(SearchStrategy strategy) {
        registry.register(strategy);
    }

    @Override
    public Collection<SearchStrategy> getStrategies() {
        return registry.getStrategies();
    }
}
