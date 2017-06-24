package ru.mydesignstudio.protege.plugin.search.strategy.attributive.weight.calculator;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator.RowWeightCalculatorSupport;

/**
 * Created by abarmin on 31.05.17.
 *
 * Калькулятор веса строки для атрибутивного поиска
 */
public class AttributiveRowWeightCalculator extends RowWeightCalculatorSupport implements WeighedRowWeightCalculator {
    public AttributiveRowWeightCalculator(SelectQuery selectQuery, SearchProcessorParams params) {
        super(selectQuery, params);
    }
}
