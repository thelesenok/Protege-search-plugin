package ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic.editor;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;

/**
 * Created by abarmin on 15.01.17.
 */
public class ButtonCellRenderer extends JButton implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value != null) {
            final String label = String.valueOf(value);
            setText(label);
        }
        return this;
    }
}
