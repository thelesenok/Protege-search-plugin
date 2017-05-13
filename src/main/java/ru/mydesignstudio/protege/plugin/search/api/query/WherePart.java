package ru.mydesignstudio.protege.plugin.search.api.query;

/**
 * Created by abarmin on 04.01.17.
 *
 * Ограничивающая часть запроса
 */
public class WherePart extends SelectField implements QueryObject {
    private LogicalOperation logicalOperation;
    private Object value;

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
        return clone;
    }
}
