package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
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
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;
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
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceAttributiveSearchIT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;
    private SearchStrategy attributiveStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
    }

    private OWLClass getOwlClass(String className) throws ApplicationException {
        return CollectionUtils.findFirst(owlService.getClasses(), new Specification<OWLClass>() {
            @Override
            public boolean isSatisfied(OWLClass owlClass) {
                return StringUtils.equalsIgnoreCase(
                        owlClass.getIRI().getFragment(),
                        className
                );
            }
        });
    }

    private OWLProperty getOwlProperty(OWLClass owlClass, String propertyName) throws ApplicationException {
        return CollectionUtils.findFirst(owlService.getDataProperties(owlClass), new Specification<OWLDataProperty>() {
            @Override
            public boolean isSatisfied(OWLDataProperty owlDataProperty) {
                return StringUtils.equalsIgnoreCase(
                        owlDataProperty.getIRI().getFragment(),
                        propertyName
                );
            }
        });
    }

    @Test
    public void testFindFelixCatByAttributeWithEqualsCondition() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass classToSelect = getOwlClass("Cat");
        selectQuery.setFrom(new FromType(classToSelect));
        selectQuery.addWherePart(new WherePart(
                classToSelect,
                getOwlProperty(classToSelect, "says"),
                LogicalOperation.EQUALS,
                "meow"
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)));
        final ResultSet resultSet = owlService.search(lookupParams);
        // Check if appropriate cat found
        assertEquals("Can't find individual by attributes", 1, resultSet.getRows().size());
        // Check if cat's weight equals to 1
        final ResultSetRow foundRow = resultSet.getRows().iterator().next();
        assertTrue("Found row is not instance of WeightedRow", foundRow instanceof WeighedRow);
        final WeighedRow weighedRow = (WeighedRow) foundRow;
        assertEquals("Incorrect weight of founded record", 1,
                weightCalculator.calculate(weighedRow.getWeight()), 0.0);
    }

    @Test
    public void testFindFelixCatByAttributeWithLikeCondition() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass classToSelect = getOwlClass("Cat");
        selectQuery.setFrom(new FromType(classToSelect));
        selectQuery.addWherePart(new WherePart(
                classToSelect,
                getOwlProperty(classToSelect, "says"),
                LogicalOperation.LIKE,
                "me"
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)));
        final ResultSet resultSet = owlService.search(lookupParams);
        // Check if cats found
        assertEquals("Can't find individual by attributes", 1, resultSet.getRows().size());
        // Check weight, weight should be 0.5
        final ResultSetRow foundRow = resultSet.getRows().iterator().next();
        assertTrue("Found row is not weighted", foundRow instanceof WeighedRow);
        final WeighedRow weighedRow = (WeighedRow) foundRow;
        assertEquals("Incorrect weight on like condition", 0.5,
                weightCalculator.calculate(weighedRow.getWeight()), 0.0);
    }
}
