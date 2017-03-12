package ru.mydesignstudio.protege.plugin.search.strategy.attributive.collector.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;

/**
 * Created by abarmin on 08.01.17.
 */
public interface LogicalOperationVisitor {
    String visit(LogicalOperation operation, String variableName, Object value);
}
