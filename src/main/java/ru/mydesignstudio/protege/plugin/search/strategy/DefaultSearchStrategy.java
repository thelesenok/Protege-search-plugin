package ru.mydesignstudio.protege.plugin.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.ui.component.search.params.basic.DefaultSearchStrategyParamsComponent;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 */
public class DefaultSearchStrategy implements SearchStrategy {
    @Inject
    private DefaultSearchStrategyParamsComponent paramsComponent;

    @Override
    public String getTitle() {
        return "Attributive lookup";
    }

    @Override
    public Component getSearchParamsPane() {
        return paramsComponent;
    }
}
