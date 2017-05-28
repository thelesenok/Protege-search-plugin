package ru.mydesignstudio.protege.plugin.search.api.result.set.sparql;

import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlResultSet implements ResultSet {
    private final Map<Integer, String> columnNames = new HashMap<>();
    private final Map<Integer, SparqlResultSetRow> rows = new HashMap<>();

    public SparqlResultSet(List<String> columnNames) {
        for (int index = 0; index < columnNames.size(); index++) {
            this.columnNames.put(index, columnNames.get(index));
        }
    }

    public void addRow(List<Object> row) {
        assert(row.size() == columnNames.size());
        final SparqlResultSetRow sparqlRow = new SparqlResultSetRow();
        for (int index = 0; index < row.size(); index++) {
            sparqlRow.addCellValue(
                    getColumnName(index),
                    row.get(index)
            );
        }
        if (!containsRow(sparqlRow)) {
            rows.put(rows.size(), sparqlRow);
        }
    }

    /**
     * Есть ли переданная строка среди уже добавленных
     * @param row - добавляемая строка
     * @return
     */
    public boolean containsRow(SparqlResultSetRow row) {
        return CollectionUtils.some(rows.values(), new Specification<SparqlResultSetRow>() {
            @Override
            public boolean isSatisfied(SparqlResultSetRow availableRow) {
                return availableRow.equals(row);
            }
        });
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
        return rows.size();
    }

    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    public Object getResult(int row, int col) {
        return rows.get(row).getCellValue(
                getColumnName(col)
        );
    }
}
