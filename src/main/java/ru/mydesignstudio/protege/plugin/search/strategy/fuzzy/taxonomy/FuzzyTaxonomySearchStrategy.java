package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy;

import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.FuzzyTaxonomyProcessor;

import javax.inject.Inject;
import java.awt.Component;

/**
 * Created by abarmin on 25.06.17.
 *
 * Стратегия поиска по онтологии с учетом таксономии. Параметры from в данном случае берутся не на основе
 * метода ближайших соседей или эквивалентных классов, а на основе параметров, указанных в аннотации
 * <fuzzyOwl2 fuzzyType="concept">
 */
public class FuzzyTaxonomySearchStrategy implements SearchStrategy {
    @Inject
    private FuzzyTaxonomyProcessor taxonomyProcessor;

    @Override
    public String getTitle() {
        return "Fuzzy taxonomy lookup";
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
        return 2;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return taxonomyProcessor;
    }
}
