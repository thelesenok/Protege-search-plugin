package ru.mydesignstudio.protege.plugin.search.ui.component.renderer;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Created by abarmin on 11.03.17.
 */
public class CellRendererWithIcon extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        super.setValue(value);
        setIcon(ComponentIconFactory.getIcon(value));
    }
}
