package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулатор "не равно"
 */
public class ProximityCalculatorNotEquals extends ProximityCalculatorSupport implements ProximityCalculator {
    @Override
    public Weight calculate(Object targetValue, OWLIndividual individual, OWLProperty property, boolean usePropertyWeight) throws ApplicationException {
        /**
         * Все время единица, так как уже отобрано sparql запросом
         */
        return Weight.maxWeight(getPropertyWeight(property, usePropertyWeight));
    }
}
