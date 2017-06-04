package ru.mydesignstudio.protege.plugin.search.api.query;

/**
 * Created by abarmin on 08.01.17.
 *
 * Результаты выполнения запроса
 */
public interface ResultSet {
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
}
