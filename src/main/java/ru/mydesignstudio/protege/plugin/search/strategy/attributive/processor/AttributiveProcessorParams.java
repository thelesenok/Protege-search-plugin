package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 12.03.17.
 *
 * Параметры атрибутивного поиска
 */
public class AttributiveProcessorParams implements SearchProcessorParams {
    private final SelectQuery selectQuery;

    public AttributiveProcessorParams(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }
}
