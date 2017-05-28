package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.FuzzyOntologyProcessor;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 13.05.17.
 */
public class FuzzyOntologySearchStrategy implements SearchStrategy {
    @Inject
    private FuzzyOntologyProcessor fuzzyProcessor;

    @Override
    public String getTitle() {
        return "Fuzzy ontology lookup";
    }

    @Override
    public <T extends Component & SearchStrategyComponent> T getSearchParamsPane() {
        return null;
    }

    @Override
    public boolean isRequired() {
        return false;
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
