package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;

import javax.inject.Inject;

/**
 * Created by abarmin on 07.01.17.
 */
@Component
public class SparqlQueryConverter {
    private final SparqlQueryVisitor visitor;

    @Inject
    public SparqlQueryConverter(SparqlQueryVisitor visitor) {
        this.visitor = visitor;
    }

    public final String convert(SelectQuery selectQuery) throws ApplicationException {
        return visitor.visit(selectQuery);
    }
}
