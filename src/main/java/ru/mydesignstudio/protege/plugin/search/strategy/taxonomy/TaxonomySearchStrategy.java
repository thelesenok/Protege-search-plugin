package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.component.TaxonomySearchParamsComponent;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 04.05.17.
 *
 * Стратегия поиска с учетом таксономической близости
 */
public class TaxonomySearchStrategy implements SearchStrategy {
    @Inject
    private TaxonomySearchParamsComponent paramsComponent;
    @Inject
    private TaxonomyProcessor taxonomyProcessor;

    @Override
    public String getTitle() {
        return "Taxonomy lookup";
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
        return 2;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return taxonomyProcessor;
    }
}
