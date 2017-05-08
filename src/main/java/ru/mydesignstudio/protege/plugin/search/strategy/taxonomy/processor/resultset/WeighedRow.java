package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.resultset;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 06.05.17.
 *
 * Строка взвешенных результатов
 */
public class WeighedRow {
    private final Map<String, Object> cells = new HashMap<>();

    /**
     * Добавить ячейку к строке
     * @param columnName - название столбца
     * @param value - значение ячейки
     */
    public void addCell(String columnName, Object value) {
        cells.put(columnName, value);
    }

    /**
     * Получить значение ячейки с указанным названием
     * @param columnName - название столбца
     * @return
     */
    public Object getCell(String columnName) {
        return cells.get(columnName);
    }
}
