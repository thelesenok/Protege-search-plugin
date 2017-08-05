package ru.mydesignstudio.protege.plugin.search.api.query;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;

/**
 * Created by abarmin on 04.01.17.
 *
 * Ограничивающая часть запроса
 */
public class WherePart extends SelectField implements QueryObject {
    /**
     * Через какой логический оператор данное условие
     * соединяется с предыдущим
     */
    private LogicalOperation concatOperation;
    /**
     * Условие сравнения
     */
    private LogicalOperation logicalOperation;
    /**
     * С чем сравниваем
     */
    private Object value;

    public WherePart() {
    }

    public WherePart(LogicalOperation concatOperation, OWLClass owlClass, OWLProperty property, LogicalOperation logicalOperation, Object value) {
        super(owlClass, property);
        //
        Validation.assertTrue(String.format(
                "%s is not a concat operation",
                concatOperation
        ), LogicalOperation.getConcatOperations().contains(concatOperation));
        //
        this.concatOperation = concatOperation;
        this.logicalOperation = logicalOperation;
        this.value = value;
    }

    public WherePart(OWLClass owlClass, OWLProperty property, LogicalOperation logicalOperation, Object value) {
        super(owlClass, property);
        this.logicalOperation = logicalOperation;
        this.value = value;
    }

    public LogicalOperation getConcatOperation() {
        return concatOperation;
    }

    public void setConcatOperation(LogicalOperation concatOperation) {
        this.concatOperation = concatOperation;
    }

    public LogicalOperation getLogicalOperation() {
        return logicalOperation;
    }

    public void setLogicalOperation(LogicalOperation logicalOperation) {
        this.logicalOperation = logicalOperation;
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
        clone.setLogicalOperation(getLogicalOperation());
        return clone;
    }
}
