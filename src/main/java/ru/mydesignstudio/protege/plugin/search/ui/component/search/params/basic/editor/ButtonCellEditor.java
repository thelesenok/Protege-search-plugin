package ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic.editor;

import ru.mydesignstudio.protege.plugin.search.service.EventBus;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic.event.RemoveRowEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by abarmin on 15.01.17.
 */
public class ButtonCellEditor extends DefaultCellEditor {
    private final JButton buttonEditor;
    private boolean isPushed = false;
    private String buttonLabel = null;
    private int currentRow;

    private EventBus eventBus = EventBus.getInstance();

    public ButtonCellEditor() {
        super(new JCheckBox());
        //
        buttonEditor = new JButton();
        buttonEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (buttonLabel == null && value != null) {
            buttonLabel = String.valueOf(value);
        }
        if (buttonLabel != null) {
            buttonEditor.setText(buttonLabel);
        }
        isPushed = true;
        currentRow = row;
        return buttonEditor;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            eventBus.publish(new RemoveRowEvent(currentRow));
        }
        isPushed = false;
        return buttonLabel == null ? "" : new String(buttonLabel);
    }
}
