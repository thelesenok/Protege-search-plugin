package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainIndividual;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 08.05.17.
 *
 * Калькулятор близости по признаку "Равно"
 */
@Component
public class ProximityCalculatorEquals extends ProximityCalculatorSupport implements ProximityCalculator {
	private final OWLService owlService;

	@Inject
	public ProximityCalculatorEquals(OWLService owlService, FuzzyOWLService fuzzyOWLService) {
		super(owlService, fuzzyOWLService);
		this.owlService = owlService;
	}

	@Override
	public Weight calculate(final Object targetValue, OWLIndividual individual, @SuppressWarnings("rawtypes") OWLProperty property, boolean usePropertyWeight) throws ApplicationException {
		final Object propertyValue = owlService.getPropertyValue(individual, property);
		if (property instanceof OWLDataProperty) {
			// вероятнее всего, targetValue - OWLDomainLiteral
			if (targetValue instanceof OWLDomainLiteral) {
				final OWLDomainLiteral domainLiteral = (OWLDomainLiteral) targetValue;
				return OWLUtils.equals(
						domainLiteral.getLiteral(),
						(OWLLiteral) propertyValue
						) ?
								Weight.maxWeight(getPropertyWeight(property, usePropertyWeight)) :
									Weight.minWeight(getPropertyWeight(property, usePropertyWeight));
			} else if (targetValue instanceof String) {
				final String stringValue = (String) targetValue;
				final OWLLiteral literalPropertyValue = (OWLLiteral) propertyValue;
				return StringUtils.equalsIgnoreCase(
						stringValue,
						literalPropertyValue.getLiteral()
						) ?
								Weight.maxWeight(getPropertyWeight(property, usePropertyWeight)) :
									Weight.minWeight(getPropertyWeight(property, usePropertyWeight));
			} else if (targetValue instanceof Boolean) {
				return Weight.maxWeight(getPropertyWeight(property, usePropertyWeight));
			} else {
				throw new ApplicationException(String.format(
						"Unsupported value type, %s",
						targetValue.getClass()
						));
			}
		} else if (property instanceof OWLObjectProperty) {
			if (targetValue instanceof OWLDomainIndividual) {
				final OWLDomainIndividual domainIndividual = (OWLDomainIndividual) targetValue;
				return OWLUtils.equals(
						domainIndividual.getNamedIndividual(),
						(OWLNamedIndividual) propertyValue
						) ?
								Weight.maxWeight(getPropertyWeight(property, usePropertyWeight)) :
									Weight.minWeight(getPropertyWeight(property, usePropertyWeight));
			} else {
				throw new ApplicationException(String.format(
						"Unsupported value type, %s",
						targetValue.getClass()
						));
			}
		} else {
			throw new ApplicationException(String.format(
					"Unsupported value type, %s",
					targetValue.getClass()
					));
		}
	}
}
