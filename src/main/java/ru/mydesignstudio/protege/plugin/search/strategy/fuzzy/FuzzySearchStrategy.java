package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.component.FuzzyParamsComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.processor.FuzzyProcessor;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 *
 * Нечеткий поиск. Поиск по неполному совпадению текста
 */
public class FuzzySearchStrategy implements SearchStrategy {
    @Inject
    private FuzzyParamsComponent paramsComponent;
    @Inject
    private FuzzyProcessor fuzzyProcessor;

    @Override
    public String getTitle() {
        return "Fuzzy search strategy";
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
        return fuzzyProcessor;
    }
}
