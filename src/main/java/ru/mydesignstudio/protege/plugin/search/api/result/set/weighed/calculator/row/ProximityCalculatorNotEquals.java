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
 * Created by abarmin on 28.05.17.
 *
 * Калькулатор "не равно"
 */
@Component
public class ProximityCalculatorNotEquals extends ProximityCalculatorSupport implements ProximityCalculator {
	@Inject
    public ProximityCalculatorNotEquals(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		super(owlService, fuzzyOWLService);
	}

	@Override
    public Weight calculate(Object targetValue, OWLIndividual individual, @SuppressWarnings("rawtypes") OWLProperty property, boolean usePropertyWeight) throws ApplicationException {
        /**
         * Все время единица, так как уже отобрано sparql запросом
         */
        return Weight.maxWeight(getPropertyWeight(property, usePropertyWeight));
    }
}
