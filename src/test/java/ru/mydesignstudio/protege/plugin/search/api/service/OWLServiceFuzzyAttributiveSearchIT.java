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
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.FuzzyAttributiveSearchStrategy;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceFuzzyAttributiveSearchIT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;
    private SearchStrategy attributiveStrategy;
    private FuzzyAttributiveSearchStrategy fuzzyAttributiveStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology7.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        fuzzyAttributiveStrategy = InjectionUtils.getInstance(FuzzyAttributiveSearchStrategy.class);
    }

    /**
     * Ontology contains two individual with names Anna and Anne. We are looking for Anne, in this case we should
     * find Anne with weight 1 (all characters are equal) and Anna with weight 3/4 (only 3 of 4 character are equal).
     *
     * Test provided by Olesya.
     *
     * @see {https://bitbucket.org/mdsteam2017/protege-search-plugin/issues/4/7}
     * @throws Exception
     */
    @Test
    public void findByAttributeWithFuzzyStrategy() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass executorClass = TestUtils.getOwlClass(owlService,"Executor");
        selectQuery.setFrom(new FromType(executorClass));
        selectQuery.addWherePart(new WherePart(
                executorClass,
                TestUtils.getOwlProperty(owlService, executorClass, "executorName"),
                LogicalOperation.FUZZY_LIKE,
                "Anne"
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(fuzzyAttributiveStrategy, fuzzyAttributiveStrategy.getSearchStrategyParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should find both individuals
         */
        assertThat("Invalid amount of records found", resultSet.getRowCount(), equalTo(2));
        /**
         * Anne should has weight equals to 1
         */
        final ResultSetRow anneRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        resultSetRow.getObjectIRI().getFragment(),
                        "AutoTester"
                );
            }
        });
        assertThat("Can't find Anne's row", anneRow, is(notNullValue()));
        assertThat("Invalid row type", anneRow, instanceOf(WeighedRow.class));
        final WeighedRow anneWeighedRow = (WeighedRow) anneRow;
        assertThat("Invalid weight row",
                weightCalculator.calculate(anneWeighedRow.getWeight()),
                        equalTo(1.0)
        );
        /**
         * Anna should has weight equals to 3/4
         */
        final ResultSetRow annaRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        resultSetRow.getObjectIRI().getFragment(),
                        "Tester"
                );
            }
        });
        assertThat("Can't find Anna's row", annaRow, is(notNullValue()));
        assertThat("Invalid Anna's row type", annaRow, instanceOf(WeighedRow.class));
        final WeighedRow annaWeighedRow = (WeighedRow) annaRow;
        assertThat("Invalid Anna's row weight",
                weightCalculator.calculate(annaWeighedRow.getWeight()),
                equalTo(0.75)
        );
    }
}
