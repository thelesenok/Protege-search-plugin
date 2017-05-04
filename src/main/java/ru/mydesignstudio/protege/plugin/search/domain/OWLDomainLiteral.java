package ru.mydesignstudio.protege.plugin.search.domain;

import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Created by abarmin on 22.01.17.
 */
public class OWLDomainLiteral implements OWLDomainObject {
    private final OWLLiteral literal;

    public OWLDomainLiteral(OWLLiteral literal) {
        this.literal = literal;
    }

    @Override
    public String getQuotedString() {
        return "\"" + literal.getLiteral() + "\"";
    }

    @Override
    public String toString() {
        return literal.getLiteral();
    }
}
