package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.processor;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Created by abarmin on 11.05.17.
 *
 * Нечеткий критерий поиска
 */
public class FuzzyWherePart extends WherePart {
    private final int maskSize;
    private final WherePart originalWherePart;

    /**
     * @param originalWherePart - на основе какого критерия делаем нечеткий
     * @param maskSize - количество нечетких символов
     */
    public FuzzyWherePart(int maskSize, WherePart originalWherePart) {
        this.maskSize = maskSize;
        this.originalWherePart = originalWherePart;
    }

    @Override
    public OWLClass getOwlClass() {
        return originalWherePart.getOwlClass();
    }

    @Override
    public void setOwlClass(OWLClass owlClass) {
        originalWherePart.setOwlClass(owlClass);
    }

    @Override
    public LogicalOperation getLogicalOperation() {
        return originalWherePart.getLogicalOperation();
    }

    @Override
    public void setLogicalOperation(LogicalOperation logicalOperation) {
        originalWherePart.setLogicalOperation(logicalOperation);
    }

    @Override
    public OWLProperty getProperty() {
        return originalWherePart.getProperty();
    }

    @Override
    public void setProperty(OWLProperty property) {
        originalWherePart.setProperty(property);
    }

    @Override
    public Object getValue() {
        return originalWherePart.getValue();
    }

    @Override
    public void setValue(Object value) {
        originalWherePart.setValue(value);
    }

    @Override
    public WherePart clone() {
        return new FuzzyWherePart(maskSize, originalWherePart.clone());
    }

    /**
     * Сколько символов в маске для поиска
     * @return
     */
    public int getMaskSize() {
        return maskSize;
    }
}
