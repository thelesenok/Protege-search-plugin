package ru.mydesignstudio.protege.plugin.search.strategy.attributive.proximity;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.proximity.calculator.ProximityCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.proximity.calculator.ProximityCalculatorEquals;

import javax.inject.Inject;

/**
 * Created by abarmin on 08.05.17.
 *
 * Фабрика классов для вычисления близости
 */
public class ProximityCalculatorFactory {
    @Inject
    private ProximityCalculatorEquals equalsCalculator;

    /**
     * Получить калькулятор для указанной логической операции
     * @param operation - для какой логической операции нужен калькулятор
     * @param params - дополнительные параметры настройки калькулятора
     * @return - калькулятор
     * @throws ApplicationException
     */
    public ProximityCalculator getCalculator(LogicalOperation operation, TaxonomyProcessorParams params) throws ApplicationException {
        if (LogicalOperation.EQUALS.equals(operation)) {
            return equalsCalculator;
        }
        throw new ApplicationException(String.format(
                "Can't find calculator for %s operation",
                operation
        ));
    }
}
