package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor;

import org.semanticweb.owlapi.model.OWLClass;
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
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.binding.EqualClassesQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.binding.NearestNeighboursQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.weight.calculator.TaxonomyRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 04.05.17.
 *
 * Искалка с учетом таксономии
 */
public class TaxonomyProcessor extends SparqlProcessorSupport implements SearchProcessor<TaxonomyProcessorParams> {
    private final RelatedQueriesCreator nearestNeighboursCreator;
    private final RelatedQueriesCreator equalClassesCreator;
    private final TaxonomyRowWeightCalculator weightCalculator;
    private final ExceptionWrapperService wrapperService;

    private TaxonomyProcessorParams taxonomyProcessorParams;
    private AttributiveProcessorParams attributiveProcessorParams;
    private Collection<SelectQuery> relatedQueries = new ArrayList<>();

    private OWLClass targetClass;

    @Inject
	public TaxonomyProcessor(
	        OWLService owlService,
            ExceptionWrapperService wrapperService,
            SparqlQueryConverter sparqlQueryConverter,
			@NearestNeighboursQueryCreator RelatedQueriesCreator nearestNeighboursCreator, 
			@EqualClassesQueryCreator RelatedQueriesCreator equalClassesCreator,
			TaxonomyRowWeightCalculator weightCalculator) {

    	super(owlService, wrapperService, sparqlQueryConverter);
		this.nearestNeighboursCreator = nearestNeighboursCreator;
		this.equalClassesCreator = equalClassesCreator;
		this.weightCalculator = weightCalculator;
		this.wrapperService = wrapperService;
	}

	@Override
    public SelectQuery prepareQuery(SelectQuery initialQuery,
                                    TaxonomyProcessorParams strategyParams,
                                    Collection<? extends SearchProcessorParams> allParameters) throws ApplicationException {

        Validation.assertNotNull("Initial query not provided", initialQuery);
        Validation.assertNotNull("Strategy params not provided", strategyParams);
        Validation.assertNotNull("All other strategies params not provided", allParameters);
        Validation.assertFalse("Nearest neighbours and equal classes methods can not be enabled simultaneously",
                strategyParams.isNearestNeighboursMethodEnabled() &&
                        strategyParams.isEqualsClassesMethodEnabled()
        );

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
         * Save target class
         */
        this.targetClass = initialQuery.getFrom().getOwlClass();
        /**
         * сохраним параметры процессора
         */
        taxonomyProcessorParams = strategyParams;
        /**
         * на случай, если несколько раз ищем
         */
        relatedQueries = new ArrayList<>();
        /**
         * если включен метод ближайших соседей - ищем по нему
         */
        if (isNearestNeighboursMethodEnabled(strategyParams)) {
            /**
             * добавим запросы для метода ближайших соседей
             */
            relatedQueries.addAll(nearestNeighboursCreator.create(initialQuery.clone(), strategyParams));
        }
        /**
         * если включен метод эквивалентных классов - добавляем
         */
        if (isEqualClassesMethodEnabled(strategyParams)) {
            /**
             * добавим запросы для метода эквивалентных классов
             */
            relatedQueries.addAll(equalClassesCreator.create(initialQuery.clone(), strategyParams));
        }
        /**
         * далее возвращаем исходный запрос, он здесь не
         * модифицируется, а только сохраняется на всякий случай
         */
        return initialQuery;
    }

    /**
     * Включен ли метод эквивалентных классов
     * @param processorParams - параметры компонента
     * @return - признак включенности
     */
    private boolean isEqualClassesMethodEnabled(TaxonomyProcessorParams processorParams) {
        return processorParams.isEqualsClassesMethodEnabled();
    }

    /**
     * Включен ли метод ближайших соседей
     * @param processorParams - параметры компонента
     * @return - признак включенности
     */
    private boolean isNearestNeighboursMethodEnabled(TaxonomyProcessorParams processorParams) {
        return processorParams.isNearestNeighboursMethodEnabled();
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet,
                             SelectQuery selectQuery,
                             TaxonomyProcessorParams strategyParams) throws ApplicationException {

        Validation.assertNotNull("Initial result set not provided", initialResultSet);
        Validation.assertNotNull("Select query not provided", selectQuery);
        Validation.assertNotNull("Strategy params not provided", strategyParams);
        Validation.assertFalse("Nearest neighbours and equal classes methods can not be enabled simultaneously",
                strategyParams.isNearestNeighboursMethodEnabled() &&
                        strategyParams.isEqualsClassesMethodEnabled()
        );

        /**
         * Execute queries using related queries. If result sets are not weighted, weight them
         * using attributive params.
         */
        Collection<ResultSet> relatedData = new ArrayList<>();
        for (SelectQuery relatedQuery : relatedQueries) {
            final ResultSet relatedResultSet =
                    toWeightedResultSet(
                            collect(relatedQuery),
                            getAttributiveRowWeightCalculator(relatedQuery, attributiveProcessorParams)
                    );
            relatedData.add(relatedResultSet);
        }
        /**
         * так как кретерии поиска из "похожих" запросов могут быть убраны, то нужно проверить результаты,
         * содержат ли они необходимые поля и соответствуют ли их значения параметрам из исходного запроса
         */
        relatedData = CollectionUtils.map(relatedData, new Transformer<ResultSet, ResultSet>() {
            @Override
            public ResultSet transform(ResultSet relatedSet) {
                return relatedSet.filter(new Specification<ResultSetRow>() {
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
         * объединим результаты и вычислим близость
         */
        return mergeResultSets(initialResultSet, relatedData);
    }

    /**
     * Объединить все результаты
     * @param sourceData - результат выполнения исходного запроса
     * @param relatedData - результаты выполнения "близких" запросов
     * @throws ApplicationException - если вдруг что случилось
     * @return - результирующий набор данных
     */
    private ResultSet mergeResultSets(ResultSet sourceData, Collection<ResultSet> relatedData) throws ApplicationException {
        /**
         * In accordance with application architecture, sourceData is weighted result set. If we will
         * weight source data with current weight calculator, we will have an inconsistent value
         */
        Validation.assertTrue("Source data is not weighted", sourceData instanceof WeighedResultSet);
        final WeighedResultSet weightedSourceData = (WeighedResultSet) sourceData;
        for (ResultSet relatedDatum : relatedData) {
            weightedSourceData.addResultSet(relatedDatum, getTaxonomyRowWeightCalculator());
        }
        return weightedSourceData;
    }

    /**
     * Get attributive row weight calculator. It is necessary to weight rows that found using related queries.
     * @param selectQuery related select query
     * @param attributiveProcessorParams attributive processor params
     * @return attributive row weight calculator
     * @throws ApplicationException if can't create calculator
     */
    private WeighedRowWeightCalculator getAttributiveRowWeightCalculator(SelectQuery selectQuery,
                                                                         AttributiveProcessorParams attributiveProcessorParams) throws ApplicationException {

        Validation.assertNotNull("Select query not provided", selectQuery);
        Validation.assertNotNull("Attributive processor params not provided", attributiveProcessorParams);

        return new AttributiveRowWeightCalculator(selectQuery, attributiveProcessorParams);
    }

    /**
     * Калькулятор для вычисления веса строки
     * @return - объект калькулятора
     * @throws ApplicationException
     */
    private WeighedRowWeightCalculator getTaxonomyRowWeightCalculator() throws ApplicationException {
        weightCalculator.setProcessorParams(taxonomyProcessorParams);
        weightCalculator.setTargetClass(targetClass);
        return weightCalculator;
    }
}
