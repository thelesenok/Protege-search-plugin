package ru.mydesignstudio.protege.plugin.search.api.query;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;

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
}
