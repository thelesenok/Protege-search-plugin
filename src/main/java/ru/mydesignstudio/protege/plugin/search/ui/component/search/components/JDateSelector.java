package ru.mydesignstudio.protege.plugin.search.ui.component.search.components;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.Date;

/**
 * Created by abarmin on 11.03.17.
 */
@Deprecated
public class JDateSelector extends JPanel {
    private JComboBox<Integer> daySelector = new JComboBox<>();
    private JComboBox<Integer> monthSelector = new JComboBox<>();
    private JComboBox<Integer> yearSelector = new JComboBox<>();

    private Date value = new Date();

    public JDateSelector() {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(daySelector);
        add(monthSelector);
        add(yearSelector);
        //
        for (int i = 1; i < 31; i++) {
            daySelector.addItem(i);
        }
        for (int i = 1; i < 12; i++) {
            monthSelector.addItem(i);
        }
        for (int i = 1900; i < 2020; i++) {
            yearSelector.addItem(i);
        }
    }
}
