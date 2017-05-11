package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.component;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.processor.FuzzyProcessorParams;

import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Label;

/**
 * Created by abarmin on 11.05.17.
 *
 * Компонент с настройками нечеткого поиска
 */
public class FuzzyParamsComponent extends JPanel implements SearchStrategyComponent<FuzzyProcessorParams> {
    private final FuzzyProcessorParams processorParams = new FuzzyProcessorParams();
    private JTextField fuzzyMaskSize;

    public FuzzyParamsComponent() {
        setLayout(new FlowLayout());
        add(new Label("Fuzzy mask size"));
        add(createMaskSizeComponent());
        setSearchParams(processorParams);
    }

    private Component createMaskSizeComponent() {
        fuzzyMaskSize = new JTextField("", 15);
        return fuzzyMaskSize;
    }

    @Override
    public FuzzyProcessorParams getSearchParams() {
        return new FuzzyProcessorParams(
                Integer.valueOf(
                        fuzzyMaskSize.getText()
                )
        );
    }

    @Override
    public void setSearchParams(FuzzyProcessorParams params) {
        fuzzyMaskSize.setText(
                String.valueOf(params.getMaskSize())
        );
    }
}
