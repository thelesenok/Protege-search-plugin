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
 * Калькулятор для условия "содержит"
 */
@Component
public class ProximityCalculatorLike extends ProximityCalculatorSupport implements ProximityCalculator {
	@Inject
	public ProximityCalculatorLike(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		super(owlService, fuzzyOWLService);
	}

	@Override
    @SuppressWarnings("rawtypes")
    public Weight calculate(Object targetObjectValue, OWLIndividual individual, OWLProperty property, boolean usePropertyWeight) throws ApplicationException {
        /**
         * like только с буквами. Считаем, сколько букв совпало
         */
        final String propertyValue = getPropertyAsString(individual, property);
        final String targetValue = (String) targetObjectValue;
        /**
         * Вычисляем и все
         */
        final double doubleValue = (double) getCommonSymbols(propertyValue, targetValue) / propertyValue.length();
        return new Weight(doubleValue, getPropertyWeight(property, usePropertyWeight));
    }
}
