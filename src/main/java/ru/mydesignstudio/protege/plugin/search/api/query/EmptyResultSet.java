package ru.mydesignstudio.protege.plugin.search.api.query;

/**
 * Created by abarmin on 12.03.17.
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
}
