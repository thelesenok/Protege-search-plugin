package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.proximity.calculator;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;

import javax.inject.Inject;

/**
 * Created by abarmin on 08.05.17.
 *
 * Калькулятор близости по признаку "Равно"
 */
public class ProximityCalculatorEquals implements ProximityCalculator {
    @Inject
    private OWLService owlService;

    @Override
    public double calculate(final Object targetValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        final Object propertyValue = owlService.getPropertyValue(individual, property);
        if (property instanceof OWLDataProperty) {
            // вероятнее всего, targetValue - OWLDomainLiteral
            if (targetValue instanceof OWLDomainLiteral) {
                final OWLDomainLiteral domainLiteral = (OWLDomainLiteral) targetValue;
                return OWLUtils.equals(
                        domainLiteral.getLiteral(),
                        (OWLLiteral) propertyValue
                ) ? 1 : 0;
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
