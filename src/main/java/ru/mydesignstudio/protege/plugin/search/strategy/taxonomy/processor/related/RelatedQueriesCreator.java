package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

import java.util.Collection;

/**
 * Created by abarmin on 24.06.17.
 *
 * Объект, создающий схожие запросы
 */
public interface RelatedQueriesCreator {
    /**
     * Сгенерировать похожие запросы
     * @param initialQuery - исходный запрос
     * @param processorParams - параметры компонента
     * @return - коллекция похожих запросов
     * @throws ApplicationException
     */
    Collection<SelectQuery> create(SelectQuery initialQuery, SearchProcessorParams processorParams) throws ApplicationException;
}
