package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
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
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceTaxonomySearchIT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;
    //
    private SearchStrategy attributiveStrategy;
    private SearchStrategy taxonomyStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology2.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        taxonomyStrategy = InjectionUtils.getInstance(TaxonomySearchStrategy.class);
    }

    @Test
    public void testFindMatroskinCatWithEnabledTaxonomySearch() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass catClass = TestUtils.getOwlClass(owlService, "Cat");
        selectQuery.setFrom(new FromType(catClass));
        selectQuery.addWherePart(new WherePart(
                catClass,
                TestUtils.getOwlProperty(owlService, catClass, "name"),
                LogicalOperation.EQUALS,
                "Matroskin"
        ));
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy,
                new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(taxonomyStrategy, new TaxonomyProcessorParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        // Check cat found
        assertEquals("Can't find individual by params", 1, resultSet.getRowCount());
        // Check if cat is cat we looking for
        final ResultSetRow row = resultSet.getRows().iterator().next();
        final IRI foundCatRow = (IRI) row.getValue(FieldConstants.OBJECT_IRI);
        assertEquals("Incorrect record found", "Matroskin", foundCatRow.getFragment());
        /**
         * Check if row weight is correct. Row weight should be equal to 1 because class of record equals
         * to class we lookup for and property belongs to class we are looking for
         */
        assertTrue("Row is not weighted", row instanceof WeighedRow);
        final WeighedRow weighedRow = (WeighedRow) row;
        assertEquals("Row weight incorrect", 1,
                weightCalculator.calculate(weighedRow.getWeight()), 0.0);
    }

    @Test
    public void testFindSharikDogByCatParamsWithTaxonomyStrategy() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass catClass = TestUtils.getOwlClass(owlService, "Cat");
        final OWLClass dogClass = TestUtils.getOwlClass(owlService, "Dog");
        selectQuery.setFrom(new FromType(catClass));
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
        // Check dog found
        assertEquals("Can't find individual by params", 1, resultSet.getRowCount());
        // Check if dog is dog we looking for
        final ResultSetRow row = resultSet.getRows().iterator().next();
        final IRI foundDogRow = (IRI) row.getValue(FieldConstants.OBJECT_IRI);
        assertEquals("Incorrect record found", "Sharik", foundDogRow.getFragment());
        final OWLIndividual sharikIndividual = owlService.getIndividual(foundDogRow);
        assertEquals("Individula class incorrect", dogClass, owlService.getIndividualClass(sharikIndividual));
        /**
         * Check if row weight is correct. Row weight should be equals to 2/3 because we have two common
         * classes of three in hierarchy between dog and thing
         */
        assertTrue("Row is not weighted", row instanceof WeighedRow);
        final WeighedRow weighedRow = (WeighedRow) row;
        assertEquals("Row weight incorrect", (double) 2/3,
                weightCalculator.calculate(weighedRow.getWeight()), 0.0001);
    }
}