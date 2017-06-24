package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулятор близости по признаку "похоже нечетко"
 */
public class ProximityCalculatorFuzzyLike extends ProximityCalculatorSupport implements ProximityCalculator {
    @Override
    public Weight calculate(Object targetObjectValue, OWLIndividual individual, OWLProperty property) throws ApplicationException {
        final String propertyValue = getPropertyAsString(individual, property);
        final String targetValue = (String) targetObjectValue;
        //
        if (StringUtils.equalsIgnoreCase(propertyValue, targetValue)) {
            /**
             * Проверим самый простой случай, когда значения совпадают
             */
            return Weight.maxWeight();
        }
        /**
         * Посчитаем количество общих букв, так как похожесть по
         * регулярному выражению уже обеспечена за счет того, что
         * для отбора сформированы все возможные подходящие значения
         */
        double validCharacters = 0;
        for (int i = 0; i < targetValue.length(); i++) {
            final char targetChar = targetValue.charAt(i);
            if (targetChar == propertyValue.charAt(i)) {
                validCharacters = validCharacters + 1;
            } else if (propertyValue.contains(String.valueOf(targetChar))) {
                validCharacters = validCharacters + 0.5;
            }
        }
        final double doubleValue = validCharacters / propertyValue.length();
        return new Weight(doubleValue, 1);
    }
}
