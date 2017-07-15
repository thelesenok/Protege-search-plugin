package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.component;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.GridLayout;
import java.awt.Label;

/**
 * Created by abarmin on 04.05.17.
 *
 * Настройки поиска с учетом таксономической близости
 */
public class TaxonomySearchParamsComponent extends JPanel implements SearchStrategyComponent<TaxonomyProcessorParams> {
    private TaxonomyProcessorParams params = new TaxonomyProcessorParams();
    private JTextField proximityValueField;

    public TaxonomySearchParamsComponent() {
        setLayout(new GridLayout(4, 2));
        /**
         * Добавим метод ближайших соседей
         */
        addNearestNeighboursMethod(this);
        /**
         * Добавим метод эквивалентных классов
         */
        addEqualClassesMethod(this);
        /**
         * Заполнитель
         */
        add(Box.createVerticalGlue());
        add(Box.createVerticalGlue());
    }

    /**
     * Добавить параметры для метода эквивалентных классов
     * @param component
     */
    private void addEqualClassesMethod(TaxonomySearchParamsComponent component) {
        /**
         * Включалка метода
         */
        component.add(new Label("Equal classes method"));
        component.add(createEqualClassesEnabler(params));
    }

    private JCheckBox createEqualClassesEnabler(TaxonomyProcessorParams params) {
        final JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(params.isEqualsClassesMethodEnabled());
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final JCheckBox source = (JCheckBox) e.getSource();
                params.setEqualsClassesMethodEnabled(source.isSelected());
            }
        });
        return checkBox;
    }

    /**
     * Добавить параметры для метода ближайших соседей
     * @param component
     */
    private void addNearestNeighboursMethod(TaxonomySearchParamsComponent component) {
        /**
         * Включалка самого метода
         */
        component.add(new Label("Nearest neighbours method"));
        component.add(createNearestNeighboursEnabler(params));
        /**
         * Параметры метода
         */
        component.add(new Label("Taxonomy proximity"));
        component.add(createProximityEditorComponent(params));
    }

    /**
     * Сгенерировать компонент для включения метода ближайших соседей
     * @param params - поле с галкой
     * @return
     */
    private JCheckBox createNearestNeighboursEnabler(TaxonomyProcessorParams params) {
        final JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(params.isNearestNeighboursMethodEnabled());
        checkBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final JCheckBox source = (JCheckBox) e.getSource();
                proximityValueField.setEnabled(source.isSelected());
                params.setNearestNeighboursMethodEnabled(source.isSelected());
            }
        });
        return checkBox;
    }

    /**
     * Сгенерировать компонент ввода степени близости
     * @param params - параметры всей панели настройки
     * @return
     */
    private JTextField createProximityEditorComponent(TaxonomyProcessorParams params) {
        proximityValueField = new JTextField(String.valueOf(params.getProximity()), 15);
        proximityValueField.setEnabled(params.isNearestNeighboursMethodEnabled());
        return proximityValueField;
    }

    @Override
    public TaxonomyProcessorParams getSearchParams() {
        params.setProximity(
                Integer.parseInt(
                        proximityValueField.getText()
                )
        );
        return params;
    }

    @Override
    public void setSearchParams(TaxonomyProcessorParams params) {
        proximityValueField.setText(
                String.valueOf(params.getProximity())
        );
    }
}
