package ru.mydesignstudio.protege.plugin.search.strategy.attributive;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.AttributiveSearchStrategyParamsComponent;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 */
public class AttributiveSearchStrategy implements SearchStrategy {
    @Inject
    private AttributiveSearchStrategyParamsComponent paramsComponent;
    @Inject
    private AttributiveProcessor attributiveProcessor;

    @Override
    public String getTitle() {
        return "Attributive lookup";
    }

    @Override
    public Component getSearchParamsPane() {
        return paramsComponent;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return attributiveProcessor;
    }
}
