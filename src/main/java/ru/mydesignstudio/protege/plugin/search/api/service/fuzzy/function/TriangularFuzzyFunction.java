package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 13.05.17.
 *
 * Треугольная функция принадлежности
 */
public class TriangularFuzzyFunction implements FuzzyFunction {
    private final double left;
    private final double center;
    private final double right;

    public TriangularFuzzyFunction(double left, double center, double right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    @Override
    public double evaluate(int value) throws ApplicationException {
        if (value <= left) {
            return new ConstantFuzzyFunction(0).evaluate(value);
        } else if (value > left && value < center) {
            return new LinearFuzzyFunction(left, center, 0, 1).evaluate(value);
        } else if (value == center) {
            return new ConstantFuzzyFunction(1).evaluate(value);
        } else if (value > center && value <= right) {
            return new LinearFuzzyFunction(center, right, 1, 0).evaluate(value);
        } else {
            return new ConstantFuzzyFunction(0).evaluate(value);
        }
    }
}
