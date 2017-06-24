package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 08.05.17.
 *
 * Калькулятор близости по признаку "Равно"
 */
public class ProximityCalculatorEquals implements ProximityCalculator {
    private final OWLService owlService;

    public ProximityCalculatorEquals() {
        owlService = InjectionUtils.getInstance(OWLService.class);
    }

    @Override
    public Weight calculate(final Object targetValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        final Object propertyValue = owlService.getPropertyValue(individual, property);
        if (property instanceof OWLDataProperty) {
            // вероятнее всего, targetValue - OWLDomainLiteral
            if (targetValue instanceof OWLDomainLiteral) {
                final OWLDomainLiteral domainLiteral = (OWLDomainLiteral) targetValue;
                return OWLUtils.equals(
                        domainLiteral.getLiteral(),
                        (OWLLiteral) propertyValue
                ) ? Weight.maxWeight() : Weight.minWeight();
            } else if (targetValue instanceof String) {
                final String stringValue = (String) targetValue;
                final OWLLiteral literalPropertyValue = (OWLLiteral) propertyValue;
                return StringUtils.equalsIgnoreCase(
                        stringValue,
                        literalPropertyValue.getLiteral()
                ) ? Weight.maxWeight() : Weight.minWeight();
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
