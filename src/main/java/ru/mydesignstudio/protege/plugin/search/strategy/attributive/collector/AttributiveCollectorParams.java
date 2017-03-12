package ru.mydesignstudio.protege.plugin.search.strategy.attributive.collector;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchStrategyParams;

/**
 * Created by abarmin on 12.03.17.
 *
 * Параметры атрибутивного поиска
 */
public class AttributiveCollectorParams implements SearchStrategyParams {
    private final SelectQuery selectQuery;

    public AttributiveCollectorParams(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }
}
