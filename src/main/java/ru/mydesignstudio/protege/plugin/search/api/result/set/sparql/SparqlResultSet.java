package ru.mydesignstudio.protege.plugin.search.api.result.set.sparql;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlResultSet implements ResultSet {
    protected final Map<Integer, String> columnNames = new HashMap<>();
    protected final Map<Integer, ResultSetRow> rows = new HashMap<>();

    public SparqlResultSet() {
    }

    public SparqlResultSet(Collection<String> columnNames) {
        int index = 0;
        for (String columnName : columnNames) {
            addColumnName(index++, columnName);
        }
    }

    protected void addColumnName(int index, String columnName) {
        columnNames.put(index, columnName);
    }

    @Override
    public int getColumnIndex(String name) {
        final Map.Entry<Integer, String> entry = CollectionUtils.findFirst(columnNames.entrySet(), new Specification<Map.Entry<Integer, String>>() {
            @Override
            public boolean isSatisfied(Map.Entry<Integer, String> entry) {
                return StringUtils.equalsIgnoreCase(
                        entry.getValue(),
                        name
                );
            }
        });
        if (entry == null) {
            throw new ApplicationRuntimeException(String.format(
                    "There is no column with name %s",
                    name
            ));
        }
        return entry.getKey();
    }

    /**
     * Добавить запись к остальным результатам
     * @param resultSetRow - объект записи
     */
    public void addRow(ResultSetRow resultSetRow) {
        rows.put(rows.size(), resultSetRow);
    }

    /**
     * Добавить запись из результатов поиска Sparql
     * @param row - строка из результатов поиск
     * @deprecated - криво выглядит, придумать как переписать ровно
     */
    @Deprecated
    public void addRow(List<Object> row) {
        assert(row.size() == columnNames.size());
        final SparqlResultSetRow sparqlRow = new SparqlResultSetRow();
        for (int index = 0; index < row.size(); index++) {
            sparqlRow.setValue(
                    getColumnName(index),
                    row.get(index)
            );
        }
        if (!containsRow(sparqlRow)) {
            rows.put(rows.size(), sparqlRow);
        }
    }

    /**
     * Есть ли уже в ResultSet-е строка для указанной записи
     * @param row - вот для этой записи проверяем
     * @return - признак присутствия
     */
    public boolean containsRow(ResultSetRow row) {
        return CollectionUtils.some(rows.values(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow weighedRow) {
                return OWLUtils.equals(
                        weighedRow.getObjectIRI(),
                        row.getObjectIRI()
                );
            }
        });
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public int getRowCount() {
        return rows.size();
    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableCollection(columnNames.values());
    }

    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    @Override
    public Object getResult(int row, int col) {
        final ResultSetRow weighedRow = rows.get(row);
        final String columnName = getColumnName(col);
        return weighedRow.getValue(columnName);
    }

    @Override
    public Collection<ResultSetRow> getRows() {
        return Collections.unmodifiableCollection(rows.values());
    }

    @Override
    public ResultSet filter(Specification<ResultSetRow> specification) {
        Validation.assertNotNull("Specification not provided", specification);
        //
        final SparqlResultSet resultSet = new SparqlResultSet(getColumnNames());
        for (ResultSetRow row : rows.values()) {
            if (specification.isSatisfied(row)) {
                resultSet.addRow(row);
            }
        }
        return resultSet;
    }

    @Override
    public ResultSetRow getRow(int rowIndex) {
        return rows.get(rowIndex);
    }
}
