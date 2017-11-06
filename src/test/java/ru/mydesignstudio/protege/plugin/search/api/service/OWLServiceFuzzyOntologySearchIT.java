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
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.FuzzyOntologySearchStrategy;
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;
import ru.mydesignstudio.protege.plugin.search.test.TestUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceFuzzyOntologySearchIT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;

    private SearchStrategy attributiveStrategy;
    private SearchStrategy fuzzyOntologyStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology8.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        fuzzyOntologyStrategy = InjectionUtils.getInstance(FuzzyOntologySearchStrategy.class);
    }

    @Test
    public void findExecutorWithYoungAge() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass executorClass = TestUtils.getOwlClass(owlService,"Executor");
        selectQuery.setFrom(new FromType(executorClass));
        selectQuery.addWherePart(new WherePart(
                executorClass,
                TestUtils.getOwlProperty(owlService, executorClass, "executorAge"),
                LogicalOperation.EQUALS,
                "10"
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(fuzzyOntologyStrategy, fuzzyOntologyStrategy.getSearchStrategyParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should find single Developer's record
         */
        assertThat("Invalid records count", resultSet.getRowCount(), equalTo(1));
        final ResultSetRow row = resultSet.getRow(0);
        assertThat("Can't get row", row, is(notNullValue()));
        assertThat("Invalid row type", row, instanceOf(WeighedRow.class));
        final WeighedRow weighedRow = (WeighedRow) row;
        assertThat("Invalid row weight",
                weightCalculator.calculate(weighedRow.getWeight()),
                closeTo(0.33, 0.001)
        );
    }
}
