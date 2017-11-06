package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy;

import ru.mydesignstudio.protege.plugin.search.api.annotation.VisualComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.component.TaxonomySearchParamsComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessor;

import javax.inject.Inject;
import java.awt.*;

/**
 * Created by abarmin on 04.05.17.
 *
 * Стратегия поиска с учетом таксономической близости
 */
@VisualComponent
public class TaxonomySearchStrategy implements SearchStrategy {
    private final TaxonomySearchParamsComponent paramsComponent;
    private final TaxonomyProcessor taxonomyProcessor;

    @Inject
    public TaxonomySearchStrategy(TaxonomySearchParamsComponent paramsComponent, TaxonomyProcessor taxonomyProcessor) {
        this.paramsComponent = paramsComponent;
        this.taxonomyProcessor = taxonomyProcessor;
    }

    @Override
    public String getTitle() {
        return "Taxonomy lookup";
    }

    @Override
    public SearchProcessorParams getSearchStrategyParams() {
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
    public boolean canBeDisabled() {
        return true;
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
