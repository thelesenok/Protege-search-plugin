package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.component;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyCollectorParams;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.Label;

/**
 * Created by abarmin on 04.05.17.
 *
 * Настройки поиска с учетом таксономической близости
 */
public class TaxonomySearchParamsComponent extends JPanel implements SearchStrategyComponent<TaxonomyCollectorParams> {
    private TaxonomyCollectorParams params = new TaxonomyCollectorParams();
    private JTextField proximityValueField;

    public TaxonomySearchParamsComponent() {
        setLayout(new FlowLayout());
        add(new Label("Taxonomy proximity"));
        add(createProximityEditorComponent());
    }

    private JTextField createProximityEditorComponent() {
        proximityValueField = new JTextField(String.valueOf(params.getProximity()), 15);
        return proximityValueField;
    }

    @Override
    public TaxonomyCollectorParams getSearchParams() {
        params.setProximity(
                Integer.parseInt(
                        proximityValueField.getText()
                )
        );
        return params;
    }
}
