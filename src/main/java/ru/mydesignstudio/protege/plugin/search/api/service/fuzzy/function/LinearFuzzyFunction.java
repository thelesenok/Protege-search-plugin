package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

import java.math.BigDecimal;

/**
 * Created by abarmin on 13.05.17.
 *
 * Линейная функция
 */
public class LinearFuzzyFunction implements FuzzyFunction {
    private final double left;
    private final double right;
    private final double leftValue;
    private final double rightValue;

    public LinearFuzzyFunction(double left, double right, double leftValue, double rightValue) {
        this.left = left;
        this.right = right;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    @Override
    public double evaluate(int value) throws ApplicationException {
        final double b = (right * leftValue - left * rightValue) / (right - left);
        final double k = left == 0 ? (rightValue - b) / right : (leftValue - b) / left;
        final double functionValue = k * value + b;
        return BigDecimal.valueOf(functionValue)
                .setScale(2, BigDecimal.ROUND_HALF_EVEN)
                .doubleValue();
    }
}
