package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

import java.util.Collection;

/**
 * Created by abarmin on 28.05.17.
 *
 * Служебные функции для работы калькулятора близости
 */
public abstract class ProximityCalculatorSupport implements ProximityCalculator {
    private final OWLService owlService;

    public ProximityCalculatorSupport() {
        owlService = InjectionUtils.getInstance(OWLService.class);
    }

    /**
     * Значение указанного свойства у переданного экземпляра
     * @param individual - объект экземпляра
     * @param property - объект свойства
     * @return - значение свойства
     * @throws ApplicationException
     */
    public Object getPropertyValue(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        return owlService.getPropertyValue(individual, property);
    }

    /**
     * Является ли переданное свойство указанного экземпляра строковым значением
     * @param individual - объект экземпляра
     * @param property - объект свойства
     * @return - является или нет
     * @throws ApplicationException
     */
    public boolean isStringProperty(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        if (!(property instanceof OWLDataProperty)) {
            return false;
        }
        final Object propertyValue = getPropertyValue(individual, property);
        if (!(propertyValue instanceof OWLLiteralImplString)) {
            return false;
        }
        return true;
    }

    /**
     * Получить свойство указанного экземпляра
     * @param individual - объект экземпляра
     * @param property - объект свойства
     * @return - значение свойства
     * @throws ApplicationException - если свойство не является строковым
     */
    public String getPropertyAsString(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        if (!isStringProperty(individual, property)) {
            throw new ApplicationException(String.format(
                    "Property %s is not string property",
                    property.getIRI().getFragment()
            ));
        }
        final Object propertyValue = getPropertyValue(individual, property);
        return ((OWLLiteralImplString) propertyValue).getLiteral();
    }

    /**
     * Количество общих букв в двух строках
     * @param first - между этой строкой
     * @param second - и вот этой
     * @return - число общих букв
     * @throws ApplicationException
     */
    public int getCommonSymbols(String first, String second) throws ApplicationException {
        final Collection<Character> firstChars = StringUtils.toCharCollection(first);
        final Collection<Character> secondChars = StringUtils.toCharCollection(second);
        //
        firstChars.retainAll(secondChars);
        return firstChars.size();
    }
}
