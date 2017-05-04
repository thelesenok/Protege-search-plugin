package ru.mydesignstudio.protege.plugin.search.api.search.component;

/**
 * Created by abarmin on 05.01.17.
 */
public interface SearchStrategyComponent<PARAMS extends SearchProcessorParams> {
    /**
     * Параметры отбора в текущем компоненте
     *
     * @return
     */
    PARAMS getSearchParams();
}
