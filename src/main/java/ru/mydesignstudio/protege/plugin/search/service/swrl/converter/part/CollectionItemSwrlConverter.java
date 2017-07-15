package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертер коллекций сущностей в SWRL
 */
public interface CollectionItemSwrlConverter<T> {
    /**
     * Конвертировать некоторую часть {@link ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery} в SWRL
     * @param part - часть для конвертации
     * @param partNumber - номер части для конвертации
     * @return - SWRL-строка
     * @throws ApplicationException
     */
    String convert(T part, int partNumber) throws ApplicationException;
}
