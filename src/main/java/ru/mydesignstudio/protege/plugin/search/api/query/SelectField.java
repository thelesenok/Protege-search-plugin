package ru.mydesignstudio.protege.plugin.search.api.query;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * Created by abarmin on 13.05.17.
 *
 * Поле для отбора
 */
public class SelectField implements QueryObject {
    private OWLClass owlClass;
    private OWLProperty property;

    public SelectField() {
    }

    public SelectField(OWLClass owlClass, OWLProperty property) {
        this.owlClass = owlClass;
        this.property = property;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    public void setOwlClass(OWLClass owlClass) {
        this.owlClass = owlClass;
    }

    public OWLProperty getProperty() {
        return property;
    }

    public void setProperty(OWLProperty property) {
        this.property = property;
    }

    public SelectField clone() {
        final SelectField cloned = new SelectField();
        cloned.setOwlClass(getOwlClass());
        cloned.setProperty(getProperty());
        return cloned;
    }
}
