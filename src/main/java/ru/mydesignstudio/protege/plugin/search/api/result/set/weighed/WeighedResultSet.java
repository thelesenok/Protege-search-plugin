package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.sparql.SparqlResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 06.05.17.
 *
 * Взвешенные результаты
 */
public class WeighedResultSet extends SparqlResultSet implements ResultSet {
    private final WeighedRowWeightCalculator weightCalculator;

    public WeighedResultSet(ResultSet resultSet, WeighedRowWeightCalculator weightCalculator) {
        this.weightCalculator = weightCalculator;
        final ExceptionWrapperService wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
        /**
         * если нет столбца с весом, добавим его
         */
        if (!columnNames.containsKey(FieldConstants.WEIGHT)) {
            columnNames.put(columnNames.size(), FieldConstants.WEIGHT);
        }
        /**
         * скопируем названия столбцов, они потом нужны для вывода результатов
         */
        for (String sourceColumnName : resultSet.getColumnNames()) {
            if (!columnNames.containsKey(sourceColumnName) &&
                    !StringUtils.equalsIgnoreCase(sourceColumnName, FieldConstants.WEIGHT)) {
                columnNames.put(columnNames.size(), sourceColumnName);
            }
        }
        /**
         * объединяем ResultSet-ы
         */
        wrapperService.invokeWrapped(new ExceptionWrappedCallback<Void>() {
            @Override
            public Void run() throws ApplicationException {
                addResultSet(resultSet);
                return null;
            }
        });
    }

    /**
     * Получить строку для записи с указанным IRI
     * @param rowIri - IRI записи
     * @return - строка или исключение о несущствовании такой строки
     */
    private WeighedRow getRow(IRI rowIri) {
        final WeighedRow existingRow = (WeighedRow) CollectionUtils.findFirst(rows.values(), new Specification<ResultSetRow>() {
            @Override
            public boolean isSatisfied(ResultSetRow weighedRow) {
                return OWLUtils.equals(
                        weighedRow.getObjectIRI(),
                        rowIri
                );
            }
        });
        Validation.assertNotNull(String.format(
                "There is no row with IRI %s",
                rowIri
        ), existingRow);
        return existingRow;
    }

    /**
     * Добавить набор данных к взвешенному результату
     * @param resultSet - что добавляем
     * @throws ApplicationException - в случае невозможности взвешивать строки
     */
    public void addResultSet(ResultSet resultSet) throws ApplicationException {
        for (ResultSetRow sourceRow : resultSet.getRows()) {
            if (containsRow(sourceRow)) {
                /**
                 * такая запись здесь уже была, проверим, взвешенная ли она
                 */
                final WeighedRow existingRow = getRow(sourceRow.getObjectIRI());
                if (sourceRow instanceof WeighedRow) {
                    /**
                     * взвешенная, добавляем ее вес к весу имеющейся записи
                     */
                    final WeighedRow sourceWeighedRow = (WeighedRow) sourceRow;
                    existingRow.addWeight(sourceWeighedRow.getWeight());
                } else {
                    /**
                     * нет, это обычная строка, ее нужно сначала взвесить
                     */
                    final Weight calculatedWeight = weightCalculator.calculate(sourceRow);
                    existingRow.addWeight(calculatedWeight);
                }
                /**
                 * теперь нужно проверить атрибуты, вдруг можно что скопировать
                 */
                for (String sourceColumnName : resultSet.getColumnNames()) {
                    if (existingRow.getValue(sourceColumnName) == null && sourceRow.getValue(sourceColumnName) != null) {
                        existingRow.setValue(sourceColumnName, sourceRow.getValue(sourceColumnName));
                    }
                }
            } else {
                /**
                 * такой записи еще не было, в этом ResultSet-е. Перед этим надо проверить, взвешенная она или нет
                 */
                final WeighedRow rowToAdd;
                if (sourceRow instanceof WeighedRow) {
                    rowToAdd = (WeighedRow) sourceRow;
                } else {
                    final Weight calculatedWeight = weightCalculator.calculate(sourceRow);
                    rowToAdd = new WeighedRowDefaultImpl(sourceRow, calculatedWeight);
                }
                /**
                 * просто добавляем к имеющимся
                 */
                addRow(rowToAdd);
            }
        }
    }
}
