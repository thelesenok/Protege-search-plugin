package ru.mydesignstudio.protege.plugin.search.ui.model;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * Created by abarmin on 05.01.17.
 */
public class OWLUIIndividual implements OWLUIObject {
    private final OWLNamedIndividual namedIndividual;

    public OWLUIIndividual(OWLNamedIndividual namedIndividual) {
        this.namedIndividual = namedIndividual;
    }

    public OWLNamedIndividual getNamedIndividual() {
        return namedIndividual;
    }

    @Override
    public String getQuotedString() {
        return getNamedIndividual().getIRI().toQuotedString();
    }

    @Override
    public String toString() {
        return namedIndividual.getIRI().getFragment();
    }
}
