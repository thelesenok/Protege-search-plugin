package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 28.05.17.
 *
 * Функция принадлежности "Плечо слева (убывает)"
 */
public class LeftShoulderFuzzyFunction implements FuzzyFunction {
    private final double a;
    private final double b;

    public LeftShoulderFuzzyFunction(double a, double b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double evaluate(int value) throws ApplicationException {
        if (value < a) {
            return new ConstantFuzzyFunction(1).evaluate(value);
        } else if (value >= a && value < b) {
            return new LinearFuzzyFunction(a, b, 1, 0).evaluate(value);
        } else if (value >= b) {
            return new ConstantFuzzyFunction(0).evaluate(value);
        }
        return 0;
    }
}
