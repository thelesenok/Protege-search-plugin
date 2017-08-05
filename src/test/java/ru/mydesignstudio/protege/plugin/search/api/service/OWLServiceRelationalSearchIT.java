package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.AttributiveSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.RelationalSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.relational.processor.RelationalProcessorParams;
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
public class OWLServiceRelationalSearchIT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;

    private AttributiveSearchStrategy attributiveSearchStrategy;
    private RelationalSearchStrategy relationalSearchStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveSearchStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        relationalSearchStrategy = InjectionUtils.getInstance(RelationalSearchStrategy.class);
    }

    @Test
    public void testFindFelixCatByObjectPropertyGender() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass catClass = TestUtils.getOwlClass(owlService, "Cat");
        final OWLClass genderClass = TestUtils.getOwlClass(owlService, "Gender");
        selectQuery.setFrom(new FromType(catClass));
        selectQuery.addWherePart(new WherePart(
                catClass,
                TestUtils.getOwlProperty(owlService, catClass, "gender"),
                LogicalOperation.EQUALS,
                TestUtils.getOwlIndividual(owlService, genderClass, "MaleGender")
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveSearchStrategy,
                new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(relationalSearchStrategy, new RelationalProcessorParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        // Check cat is found
        assertEquals("Can't find individual by object property", 1, resultSet.getRowCount());
        // Check cat name
        final ResultSetRow row = resultSet.getRows().iterator().next();
        final IRI iri = (IRI) row.getValue(FieldConstants.OBJECT_IRI);
        assertEquals("Incorrect object found", "Felix", iri.getFragment());
        // Check weight
        assertTrue("Row is not weighted", row instanceof WeighedRow);
        final WeighedRow weighedRow = (WeighedRow) row;
        assertEquals("Row weight incorrect", 1,
                weightCalculator.calculate(weighedRow.getWeight()), 0.0);
    }

    @Test
    public void testFindFelixCatByEqualsGenderAndLikeSays() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass catClass = TestUtils.getOwlClass(owlService, "Cat");
        final OWLClass genderClass = TestUtils.getOwlClass(owlService, "Gender");
        selectQuery.setFrom(new FromType(catClass));
        selectQuery.addWherePart(new WherePart(
                catClass,
                TestUtils.getOwlProperty(owlService, catClass, "gender"),
                LogicalOperation.EQUALS,
                TestUtils.getOwlIndividual(owlService, genderClass, "MaleGender")
        ));
        selectQuery.addWherePart(new WherePart(
                LogicalOperation.AND,
                catClass,
                TestUtils.getOwlProperty(owlService, catClass, "says"),
                LogicalOperation.LIKE,
                "me"
        ));
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveSearchStrategy,
                new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(relationalSearchStrategy, new RelationalProcessorParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        // Check cat is found
        assertEquals("Can't find individual by object property", 1, resultSet.getRowCount());
        // Check cat name
        final ResultSetRow row = resultSet.getRows().iterator().next();
        final IRI iri = (IRI) row.getValue(FieldConstants.OBJECT_IRI);
        assertEquals("Incorrect object found", "Felix", iri.getFragment());
        // Check weight
        assertTrue("Row is not weighted", row instanceof WeighedRow);
        final WeighedRow weighedRow = (WeighedRow) row;
        assertEquals("Row weight incorrect", 0.75,
                weightCalculator.calculate(weighedRow.getWeight()), 0.0);
    }
}