package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулятор для условия "содержит"
 */
public class ProximityCalculatorLike extends ProximityCalculatorSupport implements ProximityCalculator {
    @Override
    public double calculate(Object targetObjectValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        /**
         * like только с буквами. Считаем, сколько букв совпало
         */
        final String propertyValue = getPropertyAsString(individual, property);
        final String targetValue = (String) targetObjectValue;
        /**
         * Вычисляем и все
         */
        return (double) getCommonSymbols(propertyValue, targetValue) / propertyValue.length();
    }
}
