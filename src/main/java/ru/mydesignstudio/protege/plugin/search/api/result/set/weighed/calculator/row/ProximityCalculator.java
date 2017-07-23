package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;

/**
 * Created by abarmin on 08.05.17.
 *
 * Интерфейс вычисления близости
 */
public interface ProximityCalculator {
    /**
     * Вычислить близость целевого значения к указанному свойству переданного объекта
     * @param targetValue - с этим свойством сравниваем
     * @param individual - объект, у которого ищем свойство
     * @param property - свойство
     * @param usePropertyWeight - использовать вес атрибута
     * @return
     * @throws ApplicationException
     */
    Weight calculate(Object targetValue, OWLIndividual individual, 
    		@SuppressWarnings("rawtypes") OWLProperty property, boolean usePropertyWeight) throws ApplicationException;
}
