package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyComponent;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.FuzzyTaxonomyProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.FuzzyTaxonomyProcessorParams;

import javax.inject.Inject;
import java.awt.*;

/**
 * Created by abarmin on 25.06.17.
 *
 * Стратегия поиска по онтологии с учетом таксономии. Параметры from в данном случае берутся не на основе
 * метода ближайших соседей или эквивалентных классов, а на основе параметров, указанных в аннотации
 * <fuzzyOwl2 fuzzyType="concept">
 */
public class FuzzyTaxonomySearchStrategy implements SearchStrategy {
    private final FuzzyTaxonomyProcessor taxonomyProcessor;
    private final FuzzyOWLService fuzzyOWLService;

    @Inject
    public FuzzyTaxonomySearchStrategy(FuzzyTaxonomyProcessor taxonomyProcessor,
                                       FuzzyOWLService fuzzyOWLService) {
        this.taxonomyProcessor = taxonomyProcessor;
        this.fuzzyOWLService = fuzzyOWLService;
    }

    @Override
    public String getTitle() {
        return "Fuzzy taxonomy lookup";
    }

    @Override
    public SearchProcessorParams getSearchStrategyParams() {
        return new FuzzyTaxonomyProcessorParams();
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
        return 2;
    }

    @Override
    public SearchProcessor getSearchProcessor() {
        return taxonomyProcessor;
    }
}
