package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулятор "Начинается на"
 */
public class ProximityCalculatorStartsWith implements ProximityCalculator {
    @Override
    public double calculate(Object targetObjectValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        /**
         * Все время единица, так как уже отображно sparql запросом
         */
        return 1;
    }
}
