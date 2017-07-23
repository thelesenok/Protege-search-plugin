package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ru.mydesignstudio.protege.plugin.search.api.common.Validation;

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
        return maxWeight(1);
    }

    /**
     * Максимальный вес с учетом указанного множителя
     * @param multiplicator - множитель
     * @return
     */
    public static final Weight maxWeight(double multiplicator) {
        return new Weight(1, multiplicator);
    }

    /**
     * Минимальный вес
     * @return
     */
    public static final Weight minWeight() {
        return minWeight(1);
    }

    /**
     * Минимальный вес с учетом указанного множителя
     * @param multiplicator - множитель
     * @return
     */
    public static final Weight minWeight(double multiplicator) {
        return new Weight(0, multiplicator);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		long temp;
		temp = Double.doubleToLongBits(multiplicator);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Weight other = (Weight) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (Double.doubleToLongBits(multiplicator) != Double.doubleToLongBits(other.multiplicator))
			return false;
		if (Double.doubleToLongBits(weight) != Double.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Weight [weight=" + weight + ", multiplicator=" + multiplicator + ", children=" + children + "]";
	}
}
