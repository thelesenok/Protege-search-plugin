package ru.mydesignstudio.protege.plugin.search.api.query;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * Created by abarmin on 04.01.17.
 */
public class FromType implements QueryObject {
    private final OWLClass owlClass;

    public FromType(OWLClass owlClass) {
        this.owlClass = owlClass;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    public FromType clone() {
        return new FromType(getOwlClass());
    }
}
