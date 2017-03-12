package ru.mydesignstudio.protege.plugin.search.api.search.collector;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyParams;

/**
 * Created by abarmin on 12.03.17.
 */
public interface SearchCollector<PARAMS extends SearchStrategyParams> {
    /**
     * Отобрать данные из онтологии на основе предыдущих данных
     * и параметров отбора
     *
     * @param initialResultSet
     * @param strategyParams
     * @return
     */
    ResultSet collect(ResultSet initialResultSet, PARAMS strategyParams) throws ApplicationException;
}
