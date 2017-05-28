package ru.mydesignstudio.protege.plugin.search.strategy.relational;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.processor.RelationalProcessor;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 04.05.17.
 *
 * Стратегия поиска "С учетом отношений"
 */
public class RelationalSearchStrategy implements SearchStrategy {
    @Inject
    private RelationalProcessor relationalProcessor;

    @Override
    public String getTitle() {
        return "Relational lookup";
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
        return 1;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return relationalProcessor;
    }
}
