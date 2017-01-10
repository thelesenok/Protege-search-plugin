package ru.mydesignstudio.protege.plugin.search.service.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Created by abarmin on 07.01.17.
 */
public interface WherePartVisitor {
    String visit(WherePart wherePart);
}
