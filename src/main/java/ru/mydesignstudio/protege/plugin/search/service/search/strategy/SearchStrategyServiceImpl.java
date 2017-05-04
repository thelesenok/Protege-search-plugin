package ru.mydesignstudio.protege.plugin.search.service.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.service.SearchStrategyService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.AttributiveSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.SemanticalSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.RelationalSearchStrategy;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by abarmin on 03.01.17.
 */
public class SearchStrategyServiceImpl implements SearchStrategyService {
    @Inject
    private SearchStrategyRegistry registry;
    @Inject
    private AttributiveSearchStrategy attributiveSearchStrategy;
    @Inject
    private RelationalSearchStrategy relationalSearchStrategy;
    @Inject
    private SemanticalSearchStrategy semanticalSearchStrategy;

    @PostConstruct
    public void init() {
        register(attributiveSearchStrategy);
        register(relationalSearchStrategy);
        register(semanticalSearchStrategy);
    }

    @Override
    public void register(SearchStrategy strategy) {
        registry.register(strategy);
    }

    @Override
    public Collection<SearchStrategy> getStrategies() {
        final List<SearchStrategy> strategies = new ArrayList<>(registry.getStrategies());
        Collections.sort(strategies, new StrategyComparator());
        return strategies;
    }

    private class StrategyComparator implements Comparator<SearchStrategy> {
        @Override
        public int compare(SearchStrategy o1, SearchStrategy o2) {
            if (o1.getOrder() == o2.getOrder()) {
                return 0;
            } else {
                return o1.getOrder() - o2.getOrder();
            }
        }
    }
}
