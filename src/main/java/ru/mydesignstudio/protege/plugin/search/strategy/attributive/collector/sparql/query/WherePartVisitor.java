package ru.mydesignstudio.protege.plugin.search.strategy.attributive.collector.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Created by abarmin on 07.01.17.
 */
public interface WherePartVisitor {
    String visit(FromType fromType, WherePart wherePart) throws ApplicationException;
}
