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
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.FuzzyTaxonomySearchStrategy;
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
import static org.hamcrest.number.IsCloseTo.closeTo;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceFuzzyTaxonomySearchIT {
    @Inject
    private OWLService owlService;
    @Inject
    private WeightCalculator weightCalculator;

    private SearchStrategy attributiveStrategy;
    private FuzzyTaxonomySearchStrategy fuzzyTaxonomyStrategy;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology9.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
        //
        attributiveStrategy = InjectionUtils.getInstance(AttributiveSearchStrategy.class);
        fuzzyTaxonomyStrategy = InjectionUtils.getInstance(FuzzyTaxonomySearchStrategy.class);
    }

    @Test
    public void findProductByProject() throws Exception {
        final SelectQuery selectQuery = new SelectQuery();
        final OWLClass projectClass = TestUtils.getOwlClass(owlService,"Project");
        selectQuery.setFrom(new FromType(projectClass));
        selectQuery.addWherePart(new WherePart(
                projectClass,
                TestUtils.getOwlProperty(owlService, projectClass, "name"),
                LogicalOperation.LIKE,
                "MS Office"
        ));
        //
        final Collection<LookupParam> lookupParams = new ArrayList<>();
        lookupParams.add(new LookupParam(attributiveStrategy, new AttributiveProcessorParams(selectQuery, false)));
        lookupParams.add(new LookupParam(fuzzyTaxonomyStrategy, fuzzyTaxonomyStrategy.getSearchStrategyParams()));
        final ResultSet resultSet = owlService.search(lookupParams);
        /**
         * We should have two records
         */
        assertThat("Invalid records count", resultSet.getRowCount(), equalTo(2));
        /**
         * MS Office Development is a Project with name "MS Office" weight should be equals to 1 because
         * (1 + 1) / 2. First 1 because of full equals on Like, second 1 is because classes are equals.
         * MS Office is a Product with name "MS Office" weight should be equals to 0.85 because
         * (1 + 0.7) / 2. First 1 because of full equals on Like, second 0.7 is because of taxonomy weight.
         *
         * Olesya approved.
         */
        final ResultSetRow projectRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        resultSetRow.getObjectIRI().getFragment(),
                        "MS_Office_Development"
                );
            }
        });
        assertThat("Can't find project's row", projectRow, is(notNullValue()));
        assertThat("Invalid project's row class", projectRow, instanceOf(WeighedRow.class));
        final WeighedRow weighedProjectRow = (WeighedRow) projectRow;
        assertThat("Invalid project's weight",
                weightCalculator.calculate(weighedProjectRow.getWeight()),
                closeTo(1.0, 0.1)
        );
        //
        final ResultSetRow productRow = CollectionUtils.findFirst(resultSet.getRows(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow resultSetRow) {
                return StringUtils.equalsIgnoreCase(
                        resultSetRow.getObjectIRI().getFragment(),
                        "MS_Office"
                );
            }
        });
        assertThat("Can't find product's row", productRow, is(notNullValue()));
        assertThat("Invalid product's row class", productRow, instanceOf(WeighedRow.class));
        final WeighedRow weighedProductRow = (WeighedRow) productRow;
        assertThat("Invalid product's weight",
                weightCalculator.calculate(weighedProductRow.getWeight()),
                closeTo(0.85, 0.1)
        );
    }
}
