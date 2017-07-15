package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертер части {@link ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery} в SWRL-строку
 */
public interface SwrlConverter<T> {
    /**
     * Конвертировать часть {@link ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery} в SWRL-строку
     * @param part - часть запроса
     * @return - строка с результатами
     * @throws ApplicationException
     */
    String convert(T part) throws ApplicationException;
}
