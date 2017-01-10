package ru.mydesignstudio.protege.plugin.search.ui.model;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 04.01.17.
 */
public class OWLUIClass implements OWLUIObject {
    private final OWLClass owlClass;

    public OWLUIClass(OWLClass owlClass) {
        this.owlClass = owlClass;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    @Override
    public String getQuotedString() {
        return getOwlClass().getIRI().toQuotedString();
    }

    @Override
    public String toString() {
        return StringUtils.substringAfter(
                owlClass.toStringID(),
                "#"
        );
    }
}
