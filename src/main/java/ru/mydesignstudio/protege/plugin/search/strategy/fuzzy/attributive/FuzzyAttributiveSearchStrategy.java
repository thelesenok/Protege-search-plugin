package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive;

import ru.mydesignstudio.protege.plugin.search.api.annotation.VisualComponent;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.component.FuzzyAttributiveParamsComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyAttributiveProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyAttributiveProcessorParams;

import javax.inject.Inject;
import java.awt.*;

/**
 * Created by abarmin on 03.01.17.
 *
 * Нечеткий поиск. Поиск по неполному совпадению текста
 */
@VisualComponent
public class FuzzyAttributiveSearchStrategy implements SearchStrategy {
    private final FuzzyAttributiveParamsComponent paramsComponent;
    private final FuzzyAttributiveProcessor fuzzyAttributiveProcessor;
    private final FuzzyOWLService fuzzyOWLService;

    @Inject
    public FuzzyAttributiveSearchStrategy(FuzzyAttributiveParamsComponent paramsComponent,
                                          FuzzyAttributiveProcessor fuzzyAttributiveProcessor,
                                          FuzzyOWLService fuzzyOWLService) {
        this.paramsComponent = paramsComponent;
        this.fuzzyAttributiveProcessor = fuzzyAttributiveProcessor;
        this.fuzzyOWLService = fuzzyOWLService;
    }

    @Override
    public String getTitle() {
        return "Fuzzy like lookup";
    }

    @Override
    public FuzzyAttributiveProcessorParams getSearchStrategyParams() {
        return paramsComponent.getSearchParams();
    }

    @Override
    public Component getSearchParamsPane() {
        return paramsComponent;
    }

    @Override
    public boolean enabledByDefault() {
        return false;
    }

    @Override
    public boolean canBeDisabled() throws ApplicationException {
        return fuzzyOWLService.isFuzzyOntology(fuzzyOWLService.getOntology());
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return fuzzyAttributiveProcessor;
    }
}
