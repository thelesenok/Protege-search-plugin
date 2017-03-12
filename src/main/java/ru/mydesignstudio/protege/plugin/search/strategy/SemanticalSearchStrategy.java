package ru.mydesignstudio.protege.plugin.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchCollector;

import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 */
@Deprecated
public class SemanticalSearchStrategy implements SearchStrategy {
    @Override
    public String getTitle() {
        return "Semantical search strategy";
    }

    @Override
    public Component getSearchParamsPane() {
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
