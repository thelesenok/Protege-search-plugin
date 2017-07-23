package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import javax.inject.Inject;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;

/**
 * Created by abarmin on 04.06.17.
 *
 * Калькулятор "Больше или равно"
 */
@Component
public class ProximityCalculatorMoreThanOrEquals extends ProximityCalculatorMoreThan {
	@Inject
	public ProximityCalculatorMoreThanOrEquals(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		super(owlService, fuzzyOWLService);
	}
}
