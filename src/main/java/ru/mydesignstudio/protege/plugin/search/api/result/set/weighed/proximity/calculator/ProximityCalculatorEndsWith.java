package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулятор "Заканчивается на"
 */
public class ProximityCalculatorEndsWith implements ProximityCalculator {
    @Override
    public double calculate(Object targetValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        /**
         * Все время единица, так как уже отобрано sparql запросом
         */
        return 1;
    }
}
