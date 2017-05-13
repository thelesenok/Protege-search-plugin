package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.component.FuzzyAttributiveParamsComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyAttributiveProcessor;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 *
 * Нечеткий поиск. Поиск по неполному совпадению текста
 */
public class FuzzyAttributiveSearchStrategy implements SearchStrategy {
    @Inject
    private FuzzyAttributiveParamsComponent paramsComponent;
    @Inject
    private FuzzyAttributiveProcessor fuzzyAttributiveProcessor;

    @Override
    public String getTitle() {
        return "Fuzzy attributive lookup";
    }

    @Override
    public Component getSearchParamsPane() {
        return paramsComponent;
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return fuzzyAttributiveProcessor;
    }
}
