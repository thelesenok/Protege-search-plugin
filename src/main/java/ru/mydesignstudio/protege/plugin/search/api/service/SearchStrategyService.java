package ru.mydesignstudio.protege.plugin.search.api.service;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;

import java.util.Collection;

/**
 * Created by abarmin on 03.01.17.
 */
public interface SearchStrategyService {
    /**
     * Регистрируем стратегию
     *
     * @param strategy
     */
    void register(SearchStrategy strategy);

    /**
     * Зарегистрированные стратегии
     *
     * @return
     */
    Collection<SearchStrategy> getStrategies();
}
