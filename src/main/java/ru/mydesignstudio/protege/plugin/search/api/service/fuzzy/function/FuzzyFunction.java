package ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 13.05.17.
 *
 * Функция принадлежности для лингвистической переменной
 */
public interface FuzzyFunction {
    /**
     * Вычислить значение функции принадлежности для указанного значения
     * @param value - значение для вычисления
     * @return - степерь принадлежности 0..1
     * @throws ApplicationException
     */
    double evaluate(int value) throws ApplicationException;
}
