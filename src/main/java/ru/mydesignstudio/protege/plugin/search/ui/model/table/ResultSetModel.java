package ru.mydesignstudio.protege.plugin.search.ui.model.table;

import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;

import javax.swing.table.AbstractTableModel;

/**
 * Created by abarmin on 08.01.17.
 */
public class ResultSetModel extends AbstractTableModel {
    private final ResultSet resultSet;

    public ResultSetModel(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public int getRowCount() {
        return resultSet.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return resultSet.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return resultSet.getResult(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return resultSet.getColumnName(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
