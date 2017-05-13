package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.function;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.TriangularFuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.xml.FuzzyOWL2;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 13.05.17.
 *
 * Фабрика функций принадлежности
 */
public class FuzzyFunctionFactory {
    /**
     * Сгенерировать функцию принадлежности на основе описания
     * @param description
     * @return
     * @throws ApplicationException
     */
    public FuzzyFunction createFunction(FuzzyOWL2 description) throws ApplicationException {
        final String functionType = description.getDatatype().getType();
        if (StringUtils.equalsIgnoreCase("triangular", functionType)) {
            return new TriangularFuzzyFunction(
                    description.getDatatype().getA(),
                    description.getDatatype().getB(),
                    description.getDatatype().getC()
            );
        } else {
            throw new ApplicationException(String.format(
                    "Function %s is not supported",
                    functionType
            ));
        }
    }
}
