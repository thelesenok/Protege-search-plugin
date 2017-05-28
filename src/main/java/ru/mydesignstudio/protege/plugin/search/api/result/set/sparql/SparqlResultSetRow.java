package ru.mydesignstudio.protege.plugin.search.api.result.set.sparql;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 13.05.17.
 *
 * Строка результатов спарклового запроса
 */
public class SparqlResultSetRow {
    private final Map<String, Object> cells = new HashMap<>();

    public void addCellValue(String columnName, Object columnValue) {
        cells.put(columnName, columnValue);
    }

    public Object getCellValue(String columnName) {
        return cells.get(columnName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SparqlResultSetRow that = (SparqlResultSetRow) o;

        return cells.equals(that.cells);
    }

    @Override
    public int hashCode() {
        return cells.hashCode();
    }
}
