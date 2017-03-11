package ru.mydesignstudio.protege.plugin.search.service.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

import javax.inject.Inject;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlQueryConverter {
    @Inject
    private SparqlQueryVisitor visitor;

    public final String convert(SelectQuery selectQuery) {
        return visitor.visit(selectQuery);
    }
}
