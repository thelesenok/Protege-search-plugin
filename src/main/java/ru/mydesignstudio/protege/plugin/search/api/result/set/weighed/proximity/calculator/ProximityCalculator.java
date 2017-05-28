package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 08.05.17.
 *
 * Интерфейс вычилсения близости
 */
public interface ProximityCalculator {
    /**
     * Вычислить близость целевого значения к указанному свойству переданного объекта
     * @param targetValue - с этим свойством сравниваем
     * @param individual - объект, у которого ищем свойство
     * @param property - свойство
     * @return
     * @throws ApplicationException
     */
    double calculate(Object targetValue, OWLIndividual individual, OWLProperty property) throws ApplicationException;
}
