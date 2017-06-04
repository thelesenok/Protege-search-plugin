package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 04.06.17.
 *
 * Калькулятор "Меньше"
 */
public class ProximityCalculatorLessThan implements ProximityCalculator {
    @Override
    public double calculate(Object targetValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        return 1;
    }
}
