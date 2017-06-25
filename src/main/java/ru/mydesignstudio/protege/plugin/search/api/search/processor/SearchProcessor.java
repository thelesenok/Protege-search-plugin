package ru.mydesignstudio.protege.plugin.search.api.search.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 12.03.17.
 *
 * Определяет класс, который формирует запрос и затем обрабатывает результат
 * выполнения этого запроса. Выполняются друг за другом в зависимости от
 * очередности выполнения стратегий
 */
public interface SearchProcessor<PARAMS extends SearchProcessorParams> {
    /**
     * Подготовить запрос для поиска на основе предыдущего запроса
     *
     * @param initialQuery
     * @param strategyParams
     * @return
     * @throws ApplicationException
     */
    SelectQuery prepareQuery(SelectQuery initialQuery, PARAMS strategyParams) throws ApplicationException;

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
