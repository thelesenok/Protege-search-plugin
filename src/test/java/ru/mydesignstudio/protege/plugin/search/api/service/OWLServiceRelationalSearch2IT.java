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
import ru.mydesignstudio.protege.plugin.search.strategy.relational.RelationalSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;
import ru.mydesignstudio.protege.plugin.search.test.TestUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceRelationalSearch2IT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;

    private AttributiveSearchStrategy attributiveSearchStrategy;
    private RelationalSearchStrategy relationalSearchStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology6.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveSearchStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        relationalSearchStrategy = InjectionUtils.getInstance(RelationalSearchStrategy.class);
    }

    @Test
    public void findProjectsWithDeveloper() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass projectClass = TestUtils.getOwlClass(owlService, "Project");
        final OWLClass executorClass = TestUtils.getOwlClass(owlService, "Executor");
        selectQuery.setFrom(new FromType(projectClass));
        selectQuery.addWherePart(new WherePart(
                projectClass,
                TestUtils.getOwlProperty(owlService, projectClass, "hasExecutor"),
                LogicalOperation.EQUALS,
                TestUtils.getOwlIndividual(owlService, executorClass, "Developer")
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveSearchStrategy,
                new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(relationalSearchStrategy, relationalSearchStrategy.getSearchStrategyParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should found both projects because both of them have developers. Weight should be equals to 1
         */
        assertThat("Invalid amount of projects", resultSet.getRowCount(), equalTo(2));
        /**
         * Check rows weight
         */
        for (ResultSetRow resultSetRow : resultSet.getRows()) {
            assertTrue("Incorrect row class", resultSetRow instanceof WeighedRow);
            final WeighedRow weighedRow = (WeighedRow) resultSetRow;
            assertThat("Invalid row weight",
                    weightCalculator.calculate(weighedRow.getWeight()),
                    equalTo(0.5));
        }
    }

    @Test
    public void findProjectWithTester() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass projectClass = TestUtils.getOwlClass(owlService, "Project");
        final OWLClass executorClass = TestUtils.getOwlClass(owlService, "Executor");
        selectQuery.setFrom(new FromType(projectClass));
        selectQuery.addWherePart(new WherePart(
                projectClass,
                TestUtils.getOwlProperty(owlService, projectClass, "hasExecutor"),
                LogicalOperation.EQUALS,
                TestUtils.getOwlIndividual(owlService, executorClass, "Tester")
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveSearchStrategy,
                new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(relationalSearchStrategy, relationalSearchStrategy.getSearchStrategyParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should found single project because only one have tester. Weight should be equals to 1
         */
        assertThat("Invalid amount of projects", resultSet.getRowCount(), equalTo(1));
        /**
         * Check rows weight
         */
        for (ResultSetRow resultSetRow : resultSet.getRows()) {
            assertTrue("Incorrect row class", resultSetRow instanceof WeighedRow);
            final WeighedRow weighedRow = (WeighedRow) resultSetRow;
            assertThat("Invalid row weight",
                    weightCalculator.calculate(weighedRow.getWeight()),
                    equalTo(0.5));
        }
    }
}
