package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;

/**
 * Created by abarmin on 04.06.17.
 *
 * Калькулятор "Меньше"
 */
public class ProximityCalculatorLessThan implements ProximityCalculator {
    @Override
    public Weight calculate(Object targetValue, OWLIndividual individual, OWLProperty property, boolean usePropertyWeight) throws ApplicationException {
        return Weight.maxWeight();
    }
}
