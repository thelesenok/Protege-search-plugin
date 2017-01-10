package ru.mydesignstudio.protege.plugin.search.strategy;

import ru.mydesignstudio.protege.plugin.search.api.SearchStrategy;

import java.awt.Component;

/**
 * Created by abarmin on 03.01.17.
 */
public class SemanticalSearchStrategy implements SearchStrategy {
    @Override
    public String getTitle() {
        return "Semantical search strategy";
    }

    @Override
    public Component getSearchParamsPane() {
        return null;
    }
}
