package ru.mydesignstudio.protege.plugin.search.api.result.set.sparql;

import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 13.05.17.
 *
 * Строка результатов спарклового запроса
 */
public class SparqlResultSetRow implements ResultSetRow {
    private final Map<String, Object> cells = new HashMap<>();

    @Override
    public Collection<String> getColumnNames() {
        return Collections.unmodifiableCollection(cells.keySet());
    }

    @Override
    public void setValue(String columnName, Object columnValue) {
        cells.put(columnName, columnValue);
    }

    @Override
    public Object getValue(String columnName) {
        return cells.get(columnName);
    }

    @Override
    public IRI getObjectIRI() {
        return (IRI) getValue(FieldConstants.OBJECT_IRI);
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
