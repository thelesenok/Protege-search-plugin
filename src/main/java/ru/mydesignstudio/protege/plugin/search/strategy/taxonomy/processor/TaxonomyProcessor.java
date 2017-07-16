package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.binding.EqualClassesQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.binding.NearestNeighboursQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.weight.calculator.TaxonomyRowWeightCalculator;

/**
 * Created by abarmin on 04.05.17.
 *
 * Искалка с учетом таксономии
 */
public class TaxonomyProcessor extends SparqlProcessorSupport implements SearchProcessor<TaxonomyProcessorParams> {
    private final RelatedQueriesCreator nearestNeighboursCreator;
    private final RelatedQueriesCreator equalClassesCreator;
    private final TaxonomyRowWeightCalculator weightCalculator;

    private TaxonomyProcessorParams processorParams;
    private Collection<SelectQuery> relatedQueries = new ArrayList<>();

    @Inject
	public TaxonomyProcessor(
			@NearestNeighboursQueryCreator RelatedQueriesCreator nearestNeighboursCreator, 
			@EqualClassesQueryCreator RelatedQueriesCreator equalClassesCreator,
			TaxonomyRowWeightCalculator weightCalculator) {
    	
		this.nearestNeighboursCreator = nearestNeighboursCreator;
		this.equalClassesCreator = equalClassesCreator;
		this.weightCalculator = weightCalculator;
	}

	@Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, TaxonomyProcessorParams strategyParams) throws ApplicationException {
        /**
         * сохраним параметры процессора
         */
        processorParams = strategyParams;
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
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, TaxonomyProcessorParams strategyParams) throws ApplicationException {
        /**
         * выполним поиск по каждому из заготовленных запросов
         */
        final Collection<ResultSet> relatedData = new ArrayList<>();
        for (SelectQuery relatedQuery : relatedQueries) {
            final ResultSet relatedResultSet = collect(relatedQuery);
            relatedData.add(relatedResultSet);
        }
        /**
         * так как кретерии поиска из "похожих" запросов могут быть убраны, то нужно проверить результаты,
         * содержат ли они необходимые поля и соответствуют ли их значения параметрам из исходного запроса
         */
        for (ResultSet relatedSet : relatedData) {
            for (ResultSetRow row : relatedSet.getRows()) {
                if (!containsSelectQueryConditionFields(row, selectQuery)) {
                    relatedSet.removeRow(row);
                }
            }
        }
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
        final WeighedResultSet resultSet = new WeighedResultSet(sourceData, getRowWeightCalculator());
        for (ResultSet relatedDatum : relatedData) {
            resultSet.addResultSet(relatedDatum);
        }
        return resultSet;
    }

    /**
     * Калькулятор для вычисления веса строки
     * @return - объект калькулятора
     * @throws ApplicationException
     */
    public WeighedRowWeightCalculator getRowWeightCalculator() throws ApplicationException {
    		weightCalculator.setProcessorParams(processorParams);
        return weightCalculator;
    }
}
