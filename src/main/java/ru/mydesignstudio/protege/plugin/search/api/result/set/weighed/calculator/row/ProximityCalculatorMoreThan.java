package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;

/**
 * Created by abarmin on 04.06.17.
 *
 * Калькулятор для логической операции "Больше чем"
 */
@Component
public class ProximityCalculatorMoreThan extends ProximityCalculatorSupport implements ProximityCalculator {
	@Inject
    public ProximityCalculatorMoreThan(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		super(owlService, fuzzyOWLService);
	}

	@Override
    public Weight calculate(Object targetValue, OWLIndividual individual, @SuppressWarnings("rawtypes") OWLProperty property, boolean usePropertyWeight) throws ApplicationException {
        return Weight.maxWeight(getPropertyWeight(property, usePropertyWeight));
    }
}
