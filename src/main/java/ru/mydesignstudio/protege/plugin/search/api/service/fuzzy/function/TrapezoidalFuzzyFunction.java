package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 28.05.17.
 *
 * Трапецеевидная функция распределения
 */
public class TrapezoidalFuzzyFunction implements FuzzyFunction {
    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public TrapezoidalFuzzyFunction(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double evaluate(int value) throws ApplicationException {
        if (value <= a) {
            return new ConstantFuzzyFunction(0).evaluate(value);
        } else if (value > a && value < b) {
            return new LinearFuzzyFunction(a, b, 0, 1).evaluate(value);
        } else if (value >= b && value < c) {
            return new ConstantFuzzyFunction(1).evaluate(value);
        } else if (value >= c && value < d) {
            return new LinearFuzzyFunction(c, d, 1, 0).evaluate(value);
        } else if (value >= d) {
            return new ConstantFuzzyFunction(0).evaluate(value);
        }
        return 0;
    }
}
