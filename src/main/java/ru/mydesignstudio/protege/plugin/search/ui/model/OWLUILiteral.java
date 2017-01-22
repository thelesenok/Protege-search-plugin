package ru.mydesignstudio.protege.plugin.search.ui.model;

import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Created by abarmin on 22.01.17.
 */
public class OWLUILiteral implements OWLUIObject {
    private final OWLLiteral literal;

    public OWLUILiteral(OWLLiteral literal) {
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
