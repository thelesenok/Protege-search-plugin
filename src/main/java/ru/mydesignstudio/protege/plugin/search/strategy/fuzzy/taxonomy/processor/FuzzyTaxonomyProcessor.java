package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryConverter;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.weight.calculator.AttributiveRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related.binding.FuzzyQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.weight.calculator.FuzzyTaxonomyRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 25.06.17.
 *
 * Искалка по таксономии с возможностью получения параметров из аннотаций
 */
@Component
public class FuzzyTaxonomyProcessor extends SparqlProcessorSupport implements SearchProcessor<FuzzyTaxonomyProcessorParams> {
    private final Collection<SelectQuery> relatedQueries = new ArrayList<>();
    private final RelatedQueriesCreator queriesCreator;
    private final ExceptionWrapperService wrapperService;

    private AttributiveProcessorParams attributiveProcessorParams;

    @Inject
    public FuzzyTaxonomyProcessor(@FuzzyQueryCreator RelatedQueriesCreator queriesCreator,
                                  final OWLService owlService,
                                  final ExceptionWrapperService wrapperService,
                                  final SparqlQueryConverter sparqlQueryConverter) {
        super(owlService, wrapperService, sparqlQueryConverter);
		this.queriesCreator = queriesCreator;
		this.wrapperService = wrapperService;
	}

	private SelectQuery initialQuery;

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery,
                                    FuzzyTaxonomyProcessorParams strategyParams,
                                    Collection<? extends SearchProcessorParams> allParameters) throws ApplicationException {

        Validation.assertNotNull("Initial query not provided", initialQuery);
        Validation.assertNotNull("Strategy params not provided", strategyParams);
        Validation.assertNotNull("Other strategies parameters not provided", allParameters);

        /**
         * Get attributive processor params for future weight calculations
         */
        for (SearchProcessorParams parameter : allParameters) {
            if (parameter instanceof AttributiveProcessorParams) {
                attributiveProcessorParams = (AttributiveProcessorParams) parameter;
            }
        }
        Validation.assertNotNull("Attributive processor params not provided", attributiveProcessorParams);

        /**
         * сохраним исходный запрос
         */
        this.initialQuery = initialQuery;
        /**
         * на случай поиска несколько раз подряд
         */
        relatedQueries.clear();
        /**
         * сгенерируем запросы с учетом данных из аннотаций
         */
        relatedQueries.addAll(queriesCreator.create(initialQuery, strategyParams));
        /**
         * исходный запрос не меняется
         */
        return initialQuery;
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet,
                             SelectQuery selectQuery,
                             FuzzyTaxonomyProcessorParams strategyParams) throws ApplicationException {

        Validation.assertNotNull("Initial result set not provided", initialResultSet);
        Validation.assertNotNull("Select query not provided", selectQuery);
        Validation.assertNotNull("Strategy params not provided", strategyParams);

        /**
         * выполним поиск по каждому из заготовленных запросов
         */
        Collection<ResultSet> relatedData = new ArrayList<>();
        for (SelectQuery query : relatedQueries) {
            relatedData.add(
                    toWeightedResultSet(
                            collect(query),
                            getAttributiveRowWeightCalculator(initialQuery, attributiveProcessorParams)
                    )
            );
        }
        /**
         * проверим, что полученные результаты хоть как-то подходят под параметры поиска
         */
        relatedData = CollectionUtils.map(relatedData, new Transformer<ResultSet, ResultSet>() {
            @Override
            public ResultSet transform(ResultSet resultSet) {
                return resultSet.filter(new Specification<ResultSetRow>() {
                    @Override
                    public boolean isSatisfied(ResultSetRow resultSetRow) {
                        return wrapperService.invokeWrapped(new ExceptionWrappedCallback<Boolean>() {
                            @Override
                            public Boolean run() throws ApplicationException {
                                return containsSelectQueryConditionFields(resultSetRow, selectQuery);
                            }
                        });
                    }
                });
            }
        });
        /**
         * объединяем все в один большой ResultSet
         */
        return mergeResultSets(initialResultSet, relatedData);
    }

    /**
     * Объединить sourceData и коллекцию найденных записей в один большой resultSet
     * @param sourceData - набор исходных данных
     * @param relatedData - записи, найденные связанными запросами
     * @return - результирующий resultSet
     * @throws ApplicationException
     */
    private ResultSet mergeResultSets(ResultSet sourceData, Collection<ResultSet> relatedData) throws ApplicationException {
        final WeighedResultSet targetResultSet = new WeighedResultSet(sourceData, getTaxonomyRowWeightCalculator(initialQuery));
        for (ResultSet resultSet : relatedData) {
            targetResultSet.addResultSet(resultSet, getTaxonomyRowWeightCalculator(initialQuery));
        }
        return targetResultSet;
    }

    private WeighedRowWeightCalculator getAttributiveRowWeightCalculator(SelectQuery initialQuery,
                                                                         AttributiveProcessorParams attributiveProcessorParams) {
        return new AttributiveRowWeightCalculator(initialQuery, attributiveProcessorParams);
    }

    /**
     * Калькулятор весов записей
     * @param initialQuery - запрос, который исходно ввел пользователь
     * @return
     */
    private WeighedRowWeightCalculator getTaxonomyRowWeightCalculator(SelectQuery initialQuery) {
        return new FuzzyTaxonomyRowWeightCalculator(initialQuery.getFrom().getOwlClass());
    }
}
