package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;

/**
 * Created by abarmin on 24.06.17.
 *
 * Вычисляет вес на основе обеъекта Weight
 */
public interface WeightCalculator {
    /**
     * Вычислить полный вес на основе всей иерархии объектов Weight
     * @param weight - объект верхнего уровня
     * @return - значение веса [0..1]
     * @throws ApplicationException
     */
    double calculate(Weight weight) throws ApplicationException;
}
