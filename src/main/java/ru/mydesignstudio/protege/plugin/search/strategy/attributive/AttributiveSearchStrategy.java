package ru.mydesignstudio.protege.plugin.search.strategy.attributive;

import java.awt.Component;

import javax.inject.Inject;

import ru.mydesignstudio.protege.plugin.search.api.annotation.VisualComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.AttributiveSearchStrategyParamsComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessor;

/**
 * Created by abarmin on 03.01.17.
 */
@VisualComponent
public class AttributiveSearchStrategy implements SearchStrategy {
    private final AttributiveSearchStrategyParamsComponent paramsComponent;
    private final AttributiveProcessor attributiveProcessor;
    
    @Inject
    public AttributiveSearchStrategy(AttributiveSearchStrategyParamsComponent paramsComponent,
			AttributiveProcessor attributiveProcessor) {
		this.paramsComponent = paramsComponent;
		this.attributiveProcessor = attributiveProcessor;
	}

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
