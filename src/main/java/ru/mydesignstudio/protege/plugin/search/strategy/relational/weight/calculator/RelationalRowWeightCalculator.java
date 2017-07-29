package ru.mydesignstudio.protege.plugin.search.strategy.relational.weight.calculator;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator.RowWeightCalculatorSupport;

/**
 * Created by abarmin on 29/07/2017.
 *
 * Row weight calculator for relational search strategy
 */
public class RelationalRowWeightCalculator extends RowWeightCalculatorSupport implements WeighedRowWeightCalculator {
    public RelationalRowWeightCalculator(SelectQuery selectQuery, SearchProcessorParams params) {
        super(selectQuery, params);
    }

    @Override
    public boolean usePropertyWeights(SearchProcessorParams processorParams) throws ApplicationException {
        return true;
    }
}
