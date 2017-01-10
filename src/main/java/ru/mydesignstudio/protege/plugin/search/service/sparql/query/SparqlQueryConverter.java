package ru.mydesignstudio.protege.plugin.search.service.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlQueryConverter {
    public static final String convert(SelectQuery selectQuery) {
        final SparqlQueryVisitor visitor = new SparqlQueryVisitor();
        return visitor.visit(selectQuery);
    }
}
