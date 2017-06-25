package ru.mydesignstudio.protege.plugin.search.api.result.set;

import org.semanticweb.owlapi.model.IRI;

import java.util.Collection;

/**
 * Created by abarmin on 24.06.17.
 *
 * Строка результатов
 */
public interface ResultSetRow {
    /**
     * Названия столбцов, которые есть в строке
     * @return - коллекция с названиями столбцов
     */
    Collection<String> getColumnNames();

    /**
     * Значение элемента в строке результата
     * @param columnName - название колонки
     * @return - значение ячейки
     */
    Object getValue(String columnName);

    /**
     * Получить идентификатор объекта, который находится в данной строке
     * @return - идентификатор
     */
    IRI getObjectIRI();

    /**
     * Установить значение ячейки
     * @param columnName - название столбца
     * @param value - значение
     */
    void setValue(String columnName, Object value);
}
