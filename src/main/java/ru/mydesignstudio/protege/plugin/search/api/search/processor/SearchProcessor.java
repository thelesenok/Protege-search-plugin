package ru.mydesignstudio.protege.plugin.search.api.search.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

import java.util.Collection;

/**
 * Created by abarmin on 12.03.17.
 *
 * Определяет класс, который формирует запрос и затем обрабатывает результат
 * выполнения этого запроса. Выполняются друг за другом в зависимости от
 * очередности выполнения стратегий
 */
public interface SearchProcessor<PARAMS extends SearchProcessorParams> {
    /**
     * Prepare query based on parameters of previous strategies.
     *
     * @param initialQuery initial query that user wrote
     * @param strategyParams current strategy parameter
     * @param allParameters parameters for all strategies
     * @return
     * @throws ApplicationException
     */
    SelectQuery prepareQuery(SelectQuery initialQuery, PARAMS strategyParams,
                             Collection<? extends SearchProcessorParams> allParameters) throws ApplicationException;

    /**
     * Отобрать данные из онтологии на основе предыдущих данных
     * и параметров отбора
     *
     * @param initialResultSet
     * @param selectQuery
     * @param strategyParams
     * @return
     */
    ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, PARAMS strategyParams) throws ApplicationException;
}
