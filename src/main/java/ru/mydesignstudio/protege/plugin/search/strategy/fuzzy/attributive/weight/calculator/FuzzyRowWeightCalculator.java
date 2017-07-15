package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.weight.calculator;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyAttributiveProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator.RowWeightCalculatorSupport;

/**
 * Created by abarmin on 28.05.17.
 *
 * Вычисляет вес строки с учетом нечетког поиска
 */
public class FuzzyRowWeightCalculator extends RowWeightCalculatorSupport implements WeighedRowWeightCalculator {
    private final SelectQuery fuzzyQuery;
    private final FuzzyAttributiveProcessorParams processorParams;

    public FuzzyRowWeightCalculator(SelectQuery selectQuery, FuzzyAttributiveProcessorParams processorParams) {
        super(selectQuery, processorParams);
        this.fuzzyQuery = selectQuery;
        this.processorParams = processorParams;
    }
}
