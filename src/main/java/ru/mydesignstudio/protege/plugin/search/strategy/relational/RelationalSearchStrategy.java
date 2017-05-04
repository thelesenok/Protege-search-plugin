package ru.mydesignstudio.protege.plugin.search.strategy.relational;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchCollector;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;

import java.awt.Component;

/**
 * Created by abarmin on 04.05.17.
 *
 * Стратегия поиска "С учетом отношений"
 */
public class RelationalSearchStrategy implements SearchStrategy {
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
    public SearchCollector getSearchCollector() {
        return null;
    }
}
