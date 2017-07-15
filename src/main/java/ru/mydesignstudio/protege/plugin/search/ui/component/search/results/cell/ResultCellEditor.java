package ru.mydesignstudio.protege.plugin.search.ui.component.search.results.cell;

import ru.mydesignstudio.protege.plugin.search.utils.Action;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by abarmin on 04.06.17.
 *
 * Редактор таблицы результатов вида "Кнопка с текстом"
 */
public class ResultCellEditor extends DefaultCellEditor {
    private final JButton editor;
    private final Action<Integer> callback;
    private boolean isPushed = false;
    private int currentRowIndex;

    public ResultCellEditor(String buttonText, Action action) {
        super(new JCheckBox());
        editor = new JButton(buttonText);
        callback = action;
        editor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        isPushed = true;
        currentRowIndex = row;
        return editor;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            try {
                callback.run(currentRowIndex);
            } finally {
                isPushed = false;
            }
        }
        return super.getCellEditorValue();
    }
}
