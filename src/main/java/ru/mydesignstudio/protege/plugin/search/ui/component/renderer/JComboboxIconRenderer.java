package ru.mydesignstudio.protege.plugin.search.ui.component.renderer;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Component;

/**
 * Created by abarmin on 11.03.17.
 */
public class JComboboxIconRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setIcon(ComponentIconFactory.getIcon(value));
        return label;
    }
}
