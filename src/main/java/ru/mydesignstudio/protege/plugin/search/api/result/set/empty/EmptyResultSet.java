package ru.mydesignstudio.protege.plugin.search.api.result.set.empty;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by abarmin on 12.03.17.
 *
 * Результаты запроса, которые ничего не содержат
 */
public class EmptyResultSet implements ResultSet {
    @Override
    public String getColumnName(int col) {
        return null;
    }

    @Override
    public Object getResult(int row, int col) {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public int getColumnIndex(String name) {
        throw new ApplicationRuntimeException(String.format(
                "There is no column with name %s",
                name
        ));
    }

    @Override
    public Collection<ResultSetRow> getRows() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getColumnNames() {
        return Collections.emptyList();
    }

    @Override
    public ResultSet filter(Specification<ResultSetRow> specification) {
        Validation.assertNotNull("Specification not provided", specification);
        return new EmptyResultSet();
    }

    @Override
    public ResultSetRow getRow(int rowIndex) {
        return null;
    }
}
