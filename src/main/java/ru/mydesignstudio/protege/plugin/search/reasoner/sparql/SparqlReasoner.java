package ru.mydesignstudio.protege.plugin.search.reasoner.sparql;

/**
 * Created by abarmin on 07.01.17.
 */
public interface SparqlReasoner {
    void precalculate() throws SparqlReasonerException;

    String getSampleQuery();

    SparqlResultSet executeQuery(String query) throws SparqlReasonerException;

    void dispose();
}
