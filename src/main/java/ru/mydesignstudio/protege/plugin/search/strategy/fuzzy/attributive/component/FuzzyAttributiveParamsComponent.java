package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.component;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyAttributiveProcessorParams;

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
public class FuzzyAttributiveParamsComponent extends JPanel implements SearchStrategyComponent<FuzzyAttributiveProcessorParams> {
    private final FuzzyAttributiveProcessorParams processorParams = new FuzzyAttributiveProcessorParams();
    private JTextField fuzzyMaskSize;

    public FuzzyAttributiveParamsComponent() {
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
    public FuzzyAttributiveProcessorParams getSearchParams() {
        return new FuzzyAttributiveProcessorParams(
                Integer.valueOf(
                        fuzzyMaskSize.getText()
                )
        );
    }

    @Override
    public void setSearchParams(FuzzyAttributiveProcessorParams params) {
        fuzzyMaskSize.setText(
                String.valueOf(params.getMaskSize())
        );
    }
}
