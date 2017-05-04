package ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.model;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainProperty;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

import javax.swing.table.AbstractTableModel;

/**
 * Created by abarmin on 04.01.17.
 */
public class CriteriaTableModel extends AbstractTableModel {
    private final SelectQuery selectQuery;
    private String[] columnNames = {
            "Class",
            "Property",
            "Logical operation",
            "Value",
            "Action"
    };

    public CriteriaTableModel(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    @Override
    public int getRowCount() {
        return selectQuery.getWhereParts().size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!selectQuery.hasWherePart(rowIndex)) {
            return null;
        }
        final WherePart wherePart = selectQuery.getWhereParts().get(rowIndex);
        if (columnIndex == 0) {
            final OWLClass owlClass = wherePart.getOwlClass();
            if (owlClass != null) {
                return new OWLDomainClass(owlClass);
            }
        } else if (columnIndex == 1) {
            final OWLProperty owlProperty = wherePart.getProperty();
            if (owlProperty != null) {
                return new OWLDomainProperty(owlProperty);
            }
        } else if (columnIndex == 2) {
            final LogicalOperation logicalOperation = wherePart.getLogicalOperation();
            if (logicalOperation != null) {
                return logicalOperation;
            }
        } else if (columnIndex == 3) {
            return wherePart.getValue();
        } else if (columnIndex == 4) {
            return rowIndex > 0 ? "Remove criteria" : "";
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!selectQuery.hasWherePart(rowIndex)) {
            return;
        }
        final WherePart wherePart = selectQuery.getWhereParts().get(rowIndex);
        if (columnIndex == 0) {
            if (aValue == null) {
                wherePart.setOwlClass(null);
            } else {
                final OWLDomainClass owluiClass = (OWLDomainClass) aValue;
                wherePart.setOwlClass(owluiClass.getOwlClass());
            }
        } else if (columnIndex == 1) {
            if (aValue == null) {
                wherePart.setProperty(null);
            } else {
                final OWLDomainProperty owluiProperty = (OWLDomainProperty) aValue;
                wherePart.setProperty(owluiProperty.getOwlProperty());
            }
        } else if (columnIndex == 2) {
            final LogicalOperation logicalOperation = (LogicalOperation) aValue;
            wherePart.setLogicalOperation(logicalOperation);
        } else if (columnIndex == 3) {
            wherePart.setValue(aValue);
        } else if (columnIndex == 4) {
            // это колонка с кнопками, для нее ничего не делаем
        } else {
            throw new ApplicationRuntimeException(String.format(
                    "Setting value to column %s is not implemented",
                    columnIndex
            ));
        }
    }
}
