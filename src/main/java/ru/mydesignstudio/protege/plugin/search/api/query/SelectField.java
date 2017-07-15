package ru.mydesignstudio.protege.plugin.search.api.query;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectField that = (SelectField) o;

        if (owlClass != null ? !OWLUtils.equals(owlClass, that.owlClass) : that.owlClass != null) return false;
        return property != null ? OWLUtils.equals(property, that.property) : that.property == null;
    }

    @Override
    public int hashCode() {
        int result = owlClass != null ? owlClass.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        return result;
    }

    public SelectField clone() {
        final SelectField cloned = new SelectField();
        cloned.setOwlClass(getOwlClass());
        cloned.setProperty(getProperty());
        return cloned;
    }
}
