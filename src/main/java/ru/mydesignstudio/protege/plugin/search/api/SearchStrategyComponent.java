package ru.mydesignstudio.protege.plugin.search.api;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

/**
 * Created by abarmin on 05.01.17.
 */
public interface SearchStrategyComponent {
    SelectQuery getSelectQuery();
}
