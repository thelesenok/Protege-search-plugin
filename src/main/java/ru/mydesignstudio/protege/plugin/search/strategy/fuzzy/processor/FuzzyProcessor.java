package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.processor;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 11.05.17.
 *
 * Процессор для нечеткого поиска
 */
public class FuzzyProcessor implements SearchProcessor<FuzzyProcessorParams> {
    private FuzzyProcessorParams processorParams;

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, FuzzyProcessorParams strategyParams) throws ApplicationException {
        processorParams = strategyParams;
        /**
         * отберем все параметры, которые содержат fuzzy условия,
         * заменим их на другие
         */
        final Collection<WherePart> preparedWhereParts = new ArrayList<>();
        for (WherePart wherePart : initialQuery.getWhereParts()) {
            if (containsFuzzyCondition(wherePart)) {
                preparedWhereParts.add(convertToFuzzyCondition(wherePart));
            } else {
                preparedWhereParts.add(wherePart);
            }
        }
        initialQuery.emptyWhereParts();
        for (WherePart wherePart : preparedWhereParts) {
            initialQuery.addWherePart(wherePart);
        }
        return initialQuery;
    }

    /**
     * Конвертировать критерий в нечеткий
     * @param wherePart
     * @return
     */
    private WherePart convertToFuzzyCondition(WherePart wherePart) {
        return new FuzzyWherePart(processorParams.getMaskSize(), wherePart);
    }

    /**
     * Проверяет, содержит ли критерий поиска нечеткое условие
     * @param wherePart - в этом условии ищем
     * @return
     */
    private boolean containsFuzzyCondition(WherePart wherePart) {
        return LogicalOperation.FUZZY_LIKE.equals(wherePart.getLogicalOperation());
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, FuzzyProcessorParams strategyParams) throws ApplicationException {
        return initialResultSet;
    }
}
