package ru.mydesignstudio.protege.plugin.search.api.service;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;

import java.util.Collection;

/**
 * Created by abarmin on 03.01.17.
 *
 * Реестр стратегий
 */
public interface SearchStrategyRegistry {
    /**
     * Зарегистрировать стратегию поиска
     * @param strategy - стратегия для регистрации
     */
    void register(SearchStrategy strategy);

    /**
     * Все стратегии для поиска
     * @return
     */
    Collection<SearchStrategy> getStrategies();

    /**
     * Стратегия по классу
     * @param strategyClass - класс стратегии
     * @param <T>
     * @return
     */
    <T extends SearchStrategy> T getStrategy(Class<T> strategyClass);
}
