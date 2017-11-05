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
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceTaxonomySearch4IT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;

    private AttributiveSearchStrategy attributiveStrategy;
    private TaxonomySearchStrategy taxonomyStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology5.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        taxonomyStrategy = InjectionUtils.getInstance(TaxonomySearchStrategy.class);
    }

    /**
     * We are looking for project with name "MS Office", but get product with name "MS Office" also
     * because this classes are equivalent. Weight of both records should be 1.
     *
     * Olesya approved.
     *
     * @throws Exception
     */
    @Test
    public void findProductByNameButGetBoth() throws Exception {
        final OWLClass projectClass = TestUtils.getOwlClass(owlService, "Project");
        //
        final SelectQuery selectQuery = new SelectQuery();
        selectQuery.setFrom(new FromType(projectClass));
        selectQuery.addWherePart(new WherePart(
                projectClass,
                TestUtils.getOwlProperty(owlService, projectClass, "name"),
                LogicalOperation.EQUALS,
                "MS Office"
        ));
        //
        final TaxonomyProcessorParams taxonomyProcessorParams = new TaxonomyProcessorParams();
        taxonomyProcessorParams.setEqualsClassesMethodEnabled(true);
        taxonomyProcessorParams.setNearestNeighboursMethodEnabled(false);
        final Collection<LookupParam> lookupParams = Arrays.asList(
                new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)),
                new LookupParam(taxonomyStrategy, taxonomyProcessorParams)
        );
        //
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * Check are both records found.
         */
        assertEquals("Incorrect records count found", 2, resultSet.getRowCount());
        /**
         * Check both rows have 1 weight
         */
        for (ResultSetRow resultSetRow : resultSet.getRows()) {
            assertTrue("Row is not weighted", resultSetRow instanceof WeighedRow);
            final WeighedRow weighedRow = (WeighedRow) resultSetRow;
            assertEquals("Invalid row weight", 1.0,
                    weightCalculator.calculate(weighedRow.getWeight()),
                    0.0
            );
        }
    }
}
