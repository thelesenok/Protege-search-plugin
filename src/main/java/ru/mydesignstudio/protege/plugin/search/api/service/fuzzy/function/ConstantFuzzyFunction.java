package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 13.05.17.
 *
 * Функция с константным значением
 */
public class ConstantFuzzyFunction implements FuzzyFunction {
    private final double value;

    public ConstantFuzzyFunction(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(int value) throws ApplicationException {
        return this.value;
    }
}
