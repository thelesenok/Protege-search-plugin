package ru.mydesignstudio.protege.plugin.search.api.result.set;

import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import java.util.Collection;

/**
 * Created by abarmin on 08.01.17.
 *
 * Результаты выполнения запроса
 */
public interface ResultSet {
    /**
     * Названия всех столбцов
     * @return - коллекция с названиями столбцов
     * @deprecated - использовать {@link ResultSetRow}
     */
    @Deprecated
    Collection<String> getColumnNames();

    /**
     * Название столбца с указанным номером
     * @param col
     * @return
     */
    String getColumnName(int col);

    /**
     * Индекс столбца с указанным названием
     * @param name - название столбца
     * @return - индекс или исключение
     */
    int getColumnIndex(String name);

    /**
     * Содержимое указанной ячейки результатов
     * @param row
     * @param col
     * @return
     */
    Object getResult(int row, int col);

    /**
     * Сколько строк (записей) в результатах
     * @return
     */
    int getRowCount();

    /**
     * Сколько столбцов (полей) в результатах
     * @return
     */
    int getColumnCount();

    /**
     * Строки в наборе результатов
     * @return - коллекция строк
     */
    Collection<ResultSetRow> getRows();

    /**
     * Запись по порядковому номеру
     * @param rowIndex - порядковый номер записи
     * @return - запись
     */
    ResultSetRow getRow(int rowIndex);

    /**
     * Create new result set that contains only records that satisfies specification.
     * @param specification specification to check
     * @return new result set
     */
    ResultSet filter(Specification<ResultSetRow> specification);
}
