package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулатор "не равно"
 */
public class ProximityCalculatorNotEquals implements ProximityCalculator {
    @Override
    public double calculate(Object targetValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        /**
         * Все время единица, так как уже отобрано sparql запросом
         */
        return 1;
    }
}
