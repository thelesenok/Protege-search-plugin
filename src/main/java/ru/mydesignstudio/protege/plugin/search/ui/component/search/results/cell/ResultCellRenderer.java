package ru.mydesignstudio.protege.plugin.search.ui.component.search.results.cell;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * Created by abarmin on 04.06.17.
 *
 * Рисовалка ячейки таблицы вида "Кнопка с текстом"
 */
public class ResultCellRenderer extends JButton implements TableCellRenderer {
    public ResultCellRenderer(String text) {
        super(text);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
