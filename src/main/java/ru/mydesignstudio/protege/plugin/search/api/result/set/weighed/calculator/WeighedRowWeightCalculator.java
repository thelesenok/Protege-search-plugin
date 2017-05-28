package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow;

/**
 * Created by abarmin on 28.05.17.
 *
 * Вычисляет вес строки
 */
public interface WeighedRowWeightCalculator {
    /**
     * Вычислить вес строки
     * @param row - строка для вычисления
     * @return - вес (0..1)
     * @throws ApplicationException
     */
    double calculate(WeighedRow row) throws ApplicationException;
}
