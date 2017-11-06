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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceFuzzyAttributiveSearch2IT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;
    private SearchStrategy attributiveStrategy;
    private FuzzyAttributiveSearchStrategy fuzzyAttributiveStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology7_1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        fuzzyAttributiveStrategy = InjectionUtils.getInstance(FuzzyAttributiveSearchStrategy.class);
    }

    @Test
    public void findAnnaByTwoFuzzyAttributes() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass executorClass = TestUtils.getOwlClass(owlService,"Executor");
        selectQuery.setFrom(new FromType(executorClass));
        selectQuery.addWherePart(new WherePart(
                executorClass,
                TestUtils.getOwlProperty(owlService, executorClass, "executorName"),
                LogicalOperation.FUZZY_LIKE,
                "Anna"
        ));
        selectQuery.addWherePart(new WherePart(
                LogicalOperation.AND,
                executorClass,
                TestUtils.getOwlProperty(owlService, executorClass, "executorLastName"),
                LogicalOperation.FUZZY_LIKE,
                "Smith"
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(fuzzyAttributiveStrategy, fuzzyAttributiveStrategy.getSearchStrategyParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should find two records.
         */
        assertThat("Invalid count of found records", resultSet.getRowCount(), equalTo(2));
        /**
         * Anna Smith has 1 weight because of full match. She is Tester
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
                equalTo(1.0)
        );
        /**
         * Anne Smile has weight equals to 6/9 because annE smiLE. She is AutoTester
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
        assertThat("Invalid Anne's row type", anneRow, instanceOf(WeighedRow.class));
        final WeighedRow anneWeighedRow = (WeighedRow) anneRow;
        /**
         * Olesya approved weight calculation way.
         */
        assertThat("Invalid Anne's row weight",
                weightCalculator.calculate(anneWeighedRow.getWeight()),
                equalTo((0.75 + 0.6) / 2)
        );
    }
}
