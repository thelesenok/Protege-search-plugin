package ru.mydesignstudio.protege.plugin.search.ui.model.table;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 08.01.17.
 */
public class ResultSetModel extends AbstractTableModel {
    private static final String USAGES_COLUMN = "Usages";
    private static final String DECLINES_COLUMN = "Declines";

    private final ResultSet resultSet;
    private final Map<Integer, String> additionalColumnNames = new HashMap<>();
    private final OWLService owlService;
    private final ExceptionWrapperService wrapperService;
    private final WeightCalculator weightCalculator;

    public ResultSetModel(ResultSet resultSet) {
        this.resultSet = resultSet;
        additionalColumnNames.put(0, USAGES_COLUMN);
        additionalColumnNames.put(1, DECLINES_COLUMN);
        additionalColumnNames.put(2, "Accept");
        additionalColumnNames.put(3, "Decline");
        //
        owlService = InjectionUtils.getInstance(OWLService.class);
        wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
        weightCalculator = InjectionUtils.getInstance(WeightCalculator.class);
    }

    @Override
    public int getRowCount() {
        return resultSet.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return resultSet.getColumnCount() + 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        /**
         * Значениия для количества применений/отказов
         */
        if (StringUtils.equalsIgnoreCase(getColumnName(columnIndex), USAGES_COLUMN)) {
            return wrapperService.invokeWrapped(new ExceptionWrappedCallback<Object>() {
                @Override
                public Object run() throws ApplicationException {
                    final IRI individualIRI = (IRI) resultSet.getResult(rowIndex, resultSet.getColumnIndex(FieldConstants.OBJECT_IRI));
                    final OWLIndividual individual = owlService.getIndividual(individualIRI);
                    return owlService.getPropertyValue(individual, FieldConstants.USAGES_COUNT);
                }
            });
        }
        if (StringUtils.equalsIgnoreCase(getColumnName(columnIndex), DECLINES_COLUMN)) {
            return wrapperService.invokeWrapped(new ExceptionWrappedCallback<Object>() {
                @Override
                public Object run() throws ApplicationException {
                    final IRI individualIRI = (IRI) resultSet.getResult(rowIndex, resultSet.getColumnIndex(FieldConstants.OBJECT_IRI));
                    final OWLIndividual individual = owlService.getIndividual(individualIRI);
                    return owlService.getPropertyValue(individual, FieldConstants.DECLINES_COUNT);
                }
            });
        }
        /**
         * Вес выводится отдельным обработчиком
         */
        if (StringUtils.equalsIgnoreCase(getColumnName(columnIndex), FieldConstants.WEIGHT)) {
            return wrapperService.invokeWrapped(new ExceptionWrappedCallback<Object>() {
                @Override
                public Object run() throws ApplicationException {
                    final ResultSetRow resultSetRow = resultSet.getRow(rowIndex);
                    if (resultSetRow instanceof WeighedRow) {
                        final WeighedRow weighedRow = (WeighedRow) resultSetRow;
                        final double calculatedWeight = weightCalculator.calculate(weighedRow.getWeight());
                        return String.format("%.2f", calculatedWeight);
                    }
                    return "-";
                }
            });
        }
        return resultSet.getResult(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        /**
         * Две последние колонки - под кнопки принять/отклонить
         */
        if (column >= resultSet.getColumnCount()) {
            return additionalColumnNames.get(column - resultSet.getColumnCount());
        }
        return resultSet.getColumnName(column);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
