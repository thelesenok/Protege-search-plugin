package ru.mydesignstudio.protege.plugin.search.reasoner.sparql;

import ru.mydesignstudio.protege.plugin.search.api.result.set.sparql.SparqlResultSet;

/**
 * Created by abarmin on 07.01.17.
 */
public interface SparqlReasoner {
    void precalculate() throws SparqlReasonerException;

    String getSampleQuery();

    SparqlResultSet executeQuery(String query) throws SparqlReasonerException;

    void dispose();
}
