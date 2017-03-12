package ru.mydesignstudio.protege.plugin.search.reasoner.sparql;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlReasonerException extends Exception {
    public SparqlReasonerException() {

    }

    public SparqlReasonerException(Throwable t) {
        super(t);
    }

    public SparqlReasonerException(String message, Throwable t) {
        super(message, t);
    }
}
