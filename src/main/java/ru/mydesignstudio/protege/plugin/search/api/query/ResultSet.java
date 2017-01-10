package ru.mydesignstudio.protege.plugin.search.api.query;

/**
 * Created by abarmin on 08.01.17.
 */
public interface ResultSet {
    String getColumnName(int col);
    Object getResult(int row, int col);
    int getRowCount();
    int getColumnCount();
}
