package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow.WEIGHT_COLUMN;

/**
 * Created by abarmin on 06.05.17.
 *
 * Взвешенные результаты
 */
public class WeighedResultSet implements ResultSet {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeighedResultSet.class);

    private final WeighedRowWeightCalculator weightCalculator;
    private final Set<WeighedRow> rows = new HashSet<>();
    private final Map<Integer, String> columnNames = new HashMap<>();

    public WeighedResultSet(ResultSet resultSet, WeighedRowWeightCalculator weightCalculator) {
        if (!(resultSet instanceof WeighedResultSet)) {
            /**
             * Добавим столбец с весом если этого еще не сделали ранее
             */
            columnNames.put(columnNames.size(), WEIGHT_COLUMN);
        }
        this.weightCalculator = weightCalculator;
        addResultSet(resultSet);
    }

    @Override
    public int getColumnIndex(String name) {
        final Map.Entry<Integer, String> entry = CollectionUtils.findFirst(columnNames.entrySet(), new Specification<Map.Entry<Integer, String>>() {
            @Override
            public boolean isSatisfied(Map.Entry<Integer, String> entry) {
                return StringUtils.equalsIgnoreCase(
                        entry.getValue(),
                        name
                );
            }
        });
        if (entry == null) {
            throw new ApplicationRuntimeException(String.format(
                    "There is no column with name %s",
                    name
            ));
        }
        return entry.getKey();
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
            if (isValidRow(row)) {
                try {
                    row.setWeight(getRowWeight(row));
                } catch (ApplicationException e) {
                    row.addCell(WEIGHT_COLUMN, 0);
                    LOGGER.warn("Can't calculate row weight", e);
                }
                rows.add(row);
            }
        }
    }

    /**
     * Походит ли строка для добавления в результат
     * @param row - строка для проверки
     * @return
     */
    protected boolean isValidRow(WeighedRow row) {
        return true;
    }

    public double getRowWeight(WeighedRow row) throws ApplicationException {
        return weightCalculator.calculate(row);
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

    protected Set<WeighedRow> getRows() {
        return rows;
    }
}
