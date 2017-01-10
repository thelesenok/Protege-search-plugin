package ru.mydesignstudio.protege.plugin.search.api;

import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 */
public interface SearchStrategy {
    /**
     * Название стратегии поиска
     *
     * @return
     */
    String getTitle();

    /**
     * Панель конфигурации поиска
     *
     * @return
     */
    <T extends Component&SearchStrategyComponent> T getSearchParamsPane();
}
