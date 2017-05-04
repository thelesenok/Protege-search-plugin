package ru.mydesignstudio.protege.plugin.search.service.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;

import java.util.Comparator;

/**
 * Created by abarmin on 04.05.17.
 *
 * Компаратор стратегий
 */
public class StrategyComparator implements Comparator<SearchStrategy> {
    @Override
    public int compare(SearchStrategy o1, SearchStrategy o2) {
        if (o1.getOrder() == o2.getOrder()) {
            return 0;
        } else {
            return o1.getOrder() - o2.getOrder();
        }
    }
}
