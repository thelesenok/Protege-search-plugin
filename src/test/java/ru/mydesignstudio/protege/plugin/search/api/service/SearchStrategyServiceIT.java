package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import javax.inject.Inject;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(GuiceJUnit4Runner.class)
public class SearchStrategyServiceIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchStrategyServiceIT.class);
    @Inject
    private OWLService owlService;
    @Inject
    private SearchStrategyService searchStrategyService;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
    }

    @Test
    public void testAvailableStrategiesForTaxonomyWithoutFuzzyProperties() throws Exception {
        final Collection<SearchStrategy> strategies = searchStrategyService.getStrategies();
        final Collection<SearchStrategy> enabled = CollectionUtils.filter(strategies, new Specification<SearchStrategy>() {
            @Override
            public boolean isSatisfied(SearchStrategy searchStrategy) {
                try {
                    return searchStrategy.enabledByDefault();
                } catch (Exception e) {
                    LOGGER.error("Can't determine if strategy enabled by default", e);
                    return false;
                }
            }
        });
        assertEquals("Can't determine enabled by default strategies",1, enabled.size());
        //
        final Collection<SearchStrategy> canBeDisabled = CollectionUtils.filter(strategies, new Specification<SearchStrategy>() {
            @Override
            public boolean isSatisfied(SearchStrategy searchStrategy) {
                try {
                    return searchStrategy.canBeDisabled();
                } catch (Exception e) {
                    LOGGER.error("Can't determine strategies can be disabled", e);
                    return false;
                }
            }
        });
        assertEquals("Can't determine strategies can be disabled", 2, canBeDisabled.size());
    }
}