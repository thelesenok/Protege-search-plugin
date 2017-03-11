package ru.mydesignstudio.protege.plugin.search.ui.component.search.components;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.Component;

/**
 * Created by abarmin on 11.03.17.
 */
@Deprecated
public class DateSelectorCellEditor extends DefaultCellEditor {
    private JDateSelector dateSelector;

    public DateSelectorCellEditor(JDateSelector dateSelector) {
        this(new JTextField());
        this.dateSelector = dateSelector;
    }

    public DateSelectorCellEditor(JTextField textField) {
        super(textField);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return dateSelector;
    }
}
