package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;

/**
 * Created by abarmin on 24.06.17.
 */
@Component
public class WeightCalculatorDefault implements WeightCalculator {
    @Override
    public double calculate(Weight weight) throws ApplicationException {
        final int totalCount = getTotalCount(weight);
        if (totalCount == 0) {
            return 0;
        }
        final double value = getTotalWeight(weight) / totalCount;
        return value;
    }

    /**
     * Общее количество подходящих для вычисления объектов-весов.
     * Подходящий имеет multiplicator > 0
     * @param weight
     * @return
     */
    private int getTotalCount(Weight weight) {
        int count = 0;
        if (isValidWeight(weight)) {
            count++;
        }
        for (Weight childWeight : weight.getChildren()) {
            count += getTotalCount(childWeight);
        }
        return count;
    }

    /**
     * Суммарный вес нижестоящих объектов с учетом того, что
     * из multiplicator > 0
     * @param weight - объект веса
     * @return - суммарный вес
     */
    private double getTotalWeight(Weight weight) {
        double totalWeight = 0;
        if (isValidWeight(weight)) {
            totalWeight += weight.getWeight();
        }
        for (Weight childWeight : weight.getChildren()) {
            totalWeight += getTotalWeight(childWeight);
        }
        return totalWeight;
    }

    /**
     * Участвует ли данный объект в подсчетах
     * @param weight - объект для проверки
     * @return - признак правильности
     */
    private boolean isValidWeight(Weight weight) {
        return weight.getMultiplicator() > 0;
    }
}
