package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import javax.inject.Inject;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулятор "Начинается на"
 */
@Component
public class ProximityCalculatorStartsWith extends ProximityCalculatorLike implements ProximityCalculator {
	@Inject
	public ProximityCalculatorStartsWith(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		super(owlService, fuzzyOWLService);
	}
}
