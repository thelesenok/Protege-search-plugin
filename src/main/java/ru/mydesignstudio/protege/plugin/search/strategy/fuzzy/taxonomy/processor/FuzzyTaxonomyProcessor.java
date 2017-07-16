package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related.binding.FuzzyQueryCreator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.weight.calculator.FuzzyTaxonomyRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.support.processor.SparqlProcessorSupport;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.related.RelatedQueriesCreator;

/**
 * Created by abarmin on 25.06.17.
 *
 * Искалка по таксономии с возможностью получения параметров из аннотаций
 */
@Component
public class FuzzyTaxonomyProcessor extends SparqlProcessorSupport implements SearchProcessor<FuzzyTaxonomyProcessorParams> {
    private final Collection<SelectQuery> relatedQueries = new ArrayList<>();
    private final RelatedQueriesCreator queriesCreator;

    @Inject
    public FuzzyTaxonomyProcessor(@FuzzyQueryCreator RelatedQueriesCreator queriesCreator) {
		this.queriesCreator = queriesCreator;
	}

	private SelectQuery initialQuery;

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, FuzzyTaxonomyProcessorParams strategyParams) throws ApplicationException {
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
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, FuzzyTaxonomyProcessorParams strategyParams) throws ApplicationException {
        /**
         * выполним поиск по каждому из заготовленных запросов
         */
        final Collection<ResultSet> relatedData = new ArrayList<>();
        for (SelectQuery query : relatedQueries) {
            relatedData.add(collect(query));
        }
        /**
         * проверим, что полученные результаты хоть как-то подходят под параметры поиска
         */
        for (ResultSet resultSet : relatedData) {
            final Collection<ResultSetRow> toRemove = new ArrayList<>();
            for (ResultSetRow resultSetRow : resultSet.getRows()) {
                if (!containsSelectQueryConditionFields(resultSetRow, selectQuery)) {
                    toRemove.add(resultSetRow);
                }
            }
            for (ResultSetRow resultSetRow : toRemove) {
                resultSet.removeRow(resultSetRow);
            }
        }
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
        final WeighedResultSet targetResultSet = new WeighedResultSet(sourceData, getWeightCalculator(initialQuery));
        for (ResultSet resultSet : relatedData) {
            targetResultSet.addResultSet(resultSet);
        }
        return targetResultSet;
    }

    /**
     * Калькулятор весов записей
     * @param initialQuery - запрос, который исходно ввел пользователь
     * @return
     */
    private WeighedRowWeightCalculator getWeightCalculator(SelectQuery initialQuery) {
        return new FuzzyTaxonomyRowWeightCalculator(initialQuery.getFrom().getOwlClass());
    }
}
