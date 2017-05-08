package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.resultset;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryVisitor;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.proximity.ProximityCalculatorFactory;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.proximity.calculator.ProximityCalculator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by abarmin on 06.05.17.
 *
 * Взвешенные результаты
 */
public class WeighedResultSet implements ResultSet {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeighedResultSet.class);

    @Inject
    private ProximityCalculatorFactory calculatorFactory;
    @Inject
    private OWLService owlService;

    private static final String WEIGHT_COLUMN = "weight";
    private final SelectQuery initialQuery;
    private final TaxonomyProcessorParams processorParams;
    private final Set<WeighedRow> rows = new HashSet<>(); //new TreeSet<>(new WeighedRowComparator());
    private final Map<Integer, String> columnNames = new HashMap<>();

    public WeighedResultSet(SelectQuery initialQuery, ResultSet resultSet, TaxonomyProcessorParams processorParams) {
        this.initialQuery = initialQuery;
        this.processorParams = processorParams;
        addResultSet(resultSet);
        columnNames.put(columnNames.size(), WEIGHT_COLUMN);
    }

    /**
     * Добавить набор данных к взвешенному результату
     * @param resultSet - что добавляем
     */
    public void addResultSet(ResultSet resultSet) {
        for (int rowIndex = 0; rowIndex < resultSet.getRowCount(); rowIndex++) {
            final WeighedRow row = new WeighedRow();
            for (int colIndex = 0; colIndex < resultSet.getColumnCount(); colIndex++) {
                final Object result = resultSet.getResult(rowIndex, colIndex);
                final String columnName = resultSet.getColumnName(colIndex);
                //
                if (!columnNames.containsValue(columnName)) {
                    columnNames.put(columnNames.size(), columnName);
                }
                row.addCell(columnName, result);
            }
            try {
                row.addCell(WEIGHT_COLUMN, String.valueOf(getRowWeight(row)));
            } catch (ApplicationException e) {
                row.addCell(WEIGHT_COLUMN, 0);
                LOGGER.warn("Can't calculate row weight", e);
            }
            rows.add(row);
        }
    }

    public double getRowWeight(WeighedRow row) throws ApplicationException {
        int paramsCount = 0;
        int weightTotal = 0;
        //
        final OWLIndividual ontologyObject = owlService.getIndividual((IRI) row.getCell(SparqlQueryVisitor.OBJECT));
        //
        for (WherePart wherePart : initialQuery.getWhereParts()) {
            final ProximityCalculator calculator = calculatorFactory.getCalculator(wherePart.getLogicalOperation(), processorParams);
            weightTotal += calculator.calculate(wherePart.getValue(), ontologyObject, wherePart.getProperty());
            paramsCount++;
        }
        //
        return paramsCount == 0 ? 0 : weightTotal / paramsCount;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }

    @Override
    public Object getResult(int row, int col) {
        // не очень быстрая имплементация, но
        // сейчас не до этого
        final List<WeighedRow> orderedRows = new LinkedList<>(rows);
        final WeighedRow weighedRow = orderedRows.get(row);
        if (weighedRow == null) {
            return null;
        }
        final String columnName = getColumnName(col);
        return weighedRow.getCell(columnName);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }
}
