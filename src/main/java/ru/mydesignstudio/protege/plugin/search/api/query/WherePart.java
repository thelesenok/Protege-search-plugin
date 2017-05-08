package ru.mydesignstudio.protege.plugin.search.api.query;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * Created by abarmin on 04.01.17.
 *
 * Ограничивающая часть запроса
 */
public class WherePart implements QueryObject {
    private OWLClass owlClass;
    private LogicalOperation logicalOperation;
    private OWLProperty property;
    private Object value;

    public OWLClass getOwlClass() {
        return owlClass;
    }

    public void setOwlClass(OWLClass owlClass) {
        this.owlClass = owlClass;
    }

    public LogicalOperation getLogicalOperation() {
        return logicalOperation;
    }

    public void setLogicalOperation(LogicalOperation logicalOperation) {
        this.logicalOperation = logicalOperation;
    }

    public OWLProperty getProperty() {
        return property;
    }

    public void setProperty(OWLProperty property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public WherePart clone() {
        final WherePart clone = new WherePart();
        clone.setOwlClass(getOwlClass());
        clone.setLogicalOperation(getLogicalOperation());
        clone.setProperty(getProperty());
        clone.setValue(getValue());
        return clone;
    }
}
