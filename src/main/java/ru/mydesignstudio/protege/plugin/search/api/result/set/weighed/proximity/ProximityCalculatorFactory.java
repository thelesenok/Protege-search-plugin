package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculator;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculatorEndsWith;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculatorEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculatorFuzzyLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculatorLike;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculatorNotEquals;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculatorStartsWith;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 08.05.17.
 *
 * Фабрика классов для вычисления близости
 */
public class ProximityCalculatorFactory {
    /**
     * Получить калькулятор для указанной логической операции
     * @param operation - для какой логической операции нужен калькулятор
     * @param params - дополнительные параметры калькулятора
     * @return - калькулятор
     * @throws ApplicationException
     */
    public ProximityCalculator getCalculator(LogicalOperation operation, SearchProcessorParams params) throws ApplicationException {
        if (LogicalOperation.EQUALS.equals(operation)) {
            return new ProximityCalculatorEquals();
        }
        if (LogicalOperation.FUZZY_LIKE.equals(operation)) {
            return new ProximityCalculatorFuzzyLike();
        }
        if (LogicalOperation.STARTS_WITH.equals(operation)) {
            return new ProximityCalculatorStartsWith();
        }
        if (LogicalOperation.ENDS_WITH.equals(operation)) {
            return new ProximityCalculatorEndsWith();
        }
        if (LogicalOperation.LIKE.equals(operation)) {
            return new ProximityCalculatorLike();
        }
        if (LogicalOperation.EQUALS_NOT.equals(operation)) {
            return new ProximityCalculatorNotEquals();
        }
        throw new ApplicationException(String.format(
                "Can't find calculator for %s operation",
                operation
        ));
    }
}
