package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by abarmin on 22.06.17.
 *
 * Объект-вес
 */
public class Weight {
    /**
     * Значение веса текущего объекта
     */
    private final double weight;
    /**
     * Множитель - разные объекты могут иметь разный вес
     * внутри одной коллекции, например, разный вес полей
     * в рамках одного результата
     */
    private final double multiplicator;
    /**
     * Веса дочерних объектов
     */
    private final Collection<Weight> children = new ArrayList<>();

    /**
     * Вес, который не участвует в расчетах
     * @return - Weight
     */
    public static final Weight noneWeight() {
        return new Weight(0, 0);
    }

    /**
     * Максимальный вес
     * @return
     */
    public static final Weight maxWeight() {
        return new Weight(1, 1);
    }

    /**
     * Минимальный вес
     * @return
     */
    public static final Weight minWeight() {
        return new Weight(0, 1);
    }

    /**
     * Создать вес
     * @param weight - текущий вес
     * @param multiplicator - множитель веса
     */
    public Weight(double weight, double multiplicator) {
        Validation.assertTrue("Weight must be [0..1]", weight >= 0 && weight <= 1);
        Validation.assertTrue("Multiplicator must be [0..1]", multiplicator >= 0 && multiplicator <= 1);
        //
        this.weight = weight;
        this.multiplicator = multiplicator;
    }

    public double getWeight() {
        return weight;
    }

    public double getMultiplicator() {
        return multiplicator;
    }

    public void addWeight(final Weight weight) {
        children.add(weight);
    }

    public Collection<Weight> getChildren() {
        return Collections.unmodifiableCollection(children);
    }
}
