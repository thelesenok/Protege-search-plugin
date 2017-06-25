package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.sparql.SparqlResultSetRow;

/**
 * Created by abarmin on 25.06.17.
 *
 * Стандартная имплементация взвешенной строки
 */
public class WeighedRowDefaultImpl extends SparqlResultSetRow implements WeighedRow {
    private final Weight weight = Weight.noneWeight();

    public WeighedRowDefaultImpl(ResultSetRow sourceRow, Weight rowWeight) {
        /**
         * Добавим вес
         */
        weight.addWeight(rowWeight);
        /**
         * Скопируем значения
         */
        for (String columnName : sourceRow.getColumnNames()) {
            setValue(columnName, sourceRow.getValue(columnName));
        }
    }

    @Override
    public Weight getWeight() {
        return weight;
    }

    @Override
    public void addWeight(Weight weight) {
        this.weight.addWeight(weight);
    }
}
