package ru.mydesignstudio.protege.plugin.search.strategy.relational;

import ru.mydesignstudio.protege.plugin.search.api.annotation.VisualComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.processor.RelationalProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.processor.RelationalProcessorParams;

import javax.inject.Inject;
import java.awt.*;

/**
 * Created by abarmin on 04.05.17.
 *
 * Стратегия поиска "С учетом отношений"
 */
@VisualComponent
public class RelationalSearchStrategy implements SearchStrategy {
    private final RelationalProcessor relationalProcessor;

    @Inject
    public RelationalSearchStrategy(RelationalProcessor relationalProcessor) {
        this.relationalProcessor = relationalProcessor;
    }

    @Override
    public String getTitle() {
        return "Relational lookup";
    }

    @Override
    public SearchProcessorParams getSearchStrategyParams() {
        return new RelationalProcessorParams();
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
    public boolean canBeDisabled() {
        return true;
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
