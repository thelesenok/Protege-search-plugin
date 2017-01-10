package ru.mydesignstudio.protege.plugin.search.service.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

/**
 * Created by abarmin on 07.01.17.
 */
public interface SelectQueryVisitor {
    String visit(SelectQuery selectQuery);
}
