package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.AttributiveSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.TaxonomySearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;
import ru.mydesignstudio.protege.plugin.search.test.TestUtils;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceTaxonomySearch3IT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;
    //
    private SearchStrategy attributiveStrategy;
    private SearchStrategy taxonomyStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology4.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        taxonomyStrategy = InjectionUtils.getInstance(TaxonomySearchStrategy.class);
    }

    @Test
    public void testFindHeroesByLegsCount() throws Exception {
        final OWLClass dogClass = TestUtils.getOwlClass(owlService, "Dog");
        //
        final SelectQuery selectQuery = new SelectQuery();
        selectQuery.setFrom(new FromType(dogClass));
        selectQuery.addWherePart(new WherePart(
                dogClass,
                TestUtils.getOwlProperty(owlService, dogClass, "legsCount"),
                LogicalOperation.MORE_OR_EQUALS,
                "2"
        ));
        //
        final TaxonomyProcessorParams taxonomyProcessorParams = new TaxonomyProcessorParams();
        taxonomyProcessorParams.setEqualsClassesMethodEnabled(false);
        taxonomyProcessorParams.setNearestNeighboursMethodEnabled(true);
        taxonomyProcessorParams.setProximity(2);
        final Collection<LookupParam> lookupParams = Arrays.asList(
                new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)),
                new LookupParam(taxonomyStrategy, taxonomyProcessorParams)
        );
        //
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should have two records because both Pluto and DarkWingDuck has more or equals to two legs.
         * But they have different weight - Pluto is dog and has more than two legs, weight should be max,
         * DarkWingDuck is not a dog, he was found by taxonomy lookup and has two legs.
         *
         * Olesya approved it.
         */
        assertEquals("Incorrect results count", 2, resultSet.getRowCount());
        /**
         * Search for rows for heroes
         */
        final ResultSetRow plutoRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        resultSetRow.getObjectIRI().getFragment(),
                        "Pluto"
                );
            }
        });
        final ResultSetRow darkWingDuckRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        resultSetRow.getObjectIRI().getFragment(),
                        "DarkWingDuck"
                );
            }
        });
        /**
         * Check heroes are here
         */
        assertNotNull("Pluto is not here", plutoRow);
        assertNotNull("DarkWingDuck is not here", darkWingDuckRow);
        /**
         * Check heroes rows weight
         */
        assertTrue("Pluto's row is not weighted", plutoRow instanceof WeighedRow);
        assertTrue("DarkWingDuck's row is not weighted", darkWingDuckRow instanceof WeighedRow);
        /**
         * Calculate and check.
         */
        assertEquals("Pluto's row has incorrect weight", 1,
                weightCalculator.calculate(((WeighedRow) plutoRow).getWeight()), 0.0);
        assertEquals("DarkWingDuck'r row has incorrect weight", (double) 5/6,
                weightCalculator.calculate(((WeighedRow) darkWingDuckRow).getWeight()), 0.0001);
    }
}
