package ru.mydesignstudio.protege.plugin.search.api.service;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
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

    /**
     * Зарегистрированная стратегия по классу
     * @param strategyClass - класс
     * @param <T>
     * @return
     * @throws ApplicationException
     */
    <T extends SearchStrategy> T getStrategy(Class<T> strategyClass) throws ApplicationException;
}
