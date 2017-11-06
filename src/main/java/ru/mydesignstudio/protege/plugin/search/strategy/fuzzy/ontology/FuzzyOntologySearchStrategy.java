package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology;

import ru.mydesignstudio.protege.plugin.search.api.annotation.VisualComponent;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.FuzzyOntologyProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.FuzzyOntologyProcessorParams;

import javax.inject.Inject;
import java.awt.*;

/**
 * Created by abarmin on 13.05.17.
 */
@VisualComponent
public class FuzzyOntologySearchStrategy implements SearchStrategy {
    private final FuzzyOntologyProcessor fuzzyProcessor;
    private final FuzzyOWLService fuzzyOWLService;

    @Inject
    public FuzzyOntologySearchStrategy(FuzzyOntologyProcessor fuzzyProcessor,
                                       FuzzyOWLService fuzzyOWLService) {
        this.fuzzyProcessor = fuzzyProcessor;
        this.fuzzyOWLService = fuzzyOWLService;
    }

    @Override
    public String getTitle() {
        return "Fuzzy ontology lookup";
    }

    @Override
    public FuzzyOntologyProcessorParams getSearchStrategyParams() {
        return new FuzzyOntologyProcessorParams();
    }

    @Override
    public <T extends Component & SearchStrategyComponent> T getSearchParamsPane() {
        return null;
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
        return 4;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return fuzzyProcessor;
    }
}
