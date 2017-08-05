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
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceTaxonomySearch2IT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;
    //
    private SearchStrategy attributiveStrategy;
    private SearchStrategy taxonomyStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology3.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        taxonomyStrategy = InjectionUtils.getInstance(TaxonomySearchStrategy.class);
    }

    @Test
    public void testFindDogSharikWithTheSameCatAndEnabledTaxonomyLookup() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass catClass = TestUtils.getOwlClass(owlService, "Cat");
        final OWLClass dogClass = TestUtils.getOwlClass(owlService, "Dog");
        selectQuery.setFrom(new FromType(dogClass));
        selectQuery.addWherePart(new WherePart(
                catClass,
                TestUtils.getOwlProperty(owlService, catClass, "name"),
                LogicalOperation.EQUALS,
                "Sharik"
        ));
        final TaxonomyProcessorParams taxonomyProcessorParams = new TaxonomyProcessorParams();
        taxonomyProcessorParams.setEqualsClassesMethodEnabled(true);
        taxonomyProcessorParams.setProximity(2);
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy,
                new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(taxonomyStrategy, taxonomyProcessorParams));
        final ResultSet resultSet = owlService.search(lookupParams);
        // Check cat and dog found
        assertEquals("Can't find individuals by params", 2, resultSet.getRowCount());
        // Get cat' and dog's rows
        final ResultSetRow sharikRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        "Sharik",
                        resultSetRow.getObjectIRI().getFragment()
                );
            }
        });
        final ResultSetRow fakeCatRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        "CloneCat",
                        resultSetRow.getObjectIRI().getFragment()
                );
            }
        });
        assertNotNull("Can't find dog's row", sharikRow);
        assertNotNull("Can't find fake cat's row", fakeCatRow);
        /**
         * Check weight.
         * Sharik the dog has 1 weight because it was found by attributes - we were looking for dog by cat'a
         * name attribute, target and Sharik's classes are equal.
         * CloneCat has weight 2/3 because we weren't found it by attributes - we were looking for dog but
         * CloneCat is a cat, and in accordance with taxonomy strategy we found it.
         */
        assertTrue("Dog's row is not weighted", sharikRow instanceof WeighedRow);
        assertTrue("CloneCat's row is not weighted", fakeCatRow instanceof WeighedRow);
        // Converting instances
        final WeighedRow weightedSharikRow = (WeighedRow) sharikRow;
        final WeighedRow weightedFakeCatRow = (WeighedRow) fakeCatRow;
        // Actually checking weight
        assertEquals("Dog's weight incorrect", 1,
                weightCalculator.calculate(weightedSharikRow.getWeight()), 0.0);
        assertEquals("CloneCat's weight incorrect", (double) 2/3,
                weightCalculator.calculate(weightedFakeCatRow.getWeight()), 0.0001);
    }
}
