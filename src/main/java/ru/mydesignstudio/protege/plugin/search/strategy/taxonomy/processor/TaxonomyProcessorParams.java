package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 04.05.17.
 *
 * Параметры поиска с учетом таксономической близости
 */
public class TaxonomyProcessorParams implements SearchProcessorParams {
    /**
     * Степень близости по умолчанию
     */
    private int proximity = 3;
    /**
     * Использовать ли метод ближайших соседей
     */
    private boolean nearestNeighboursMethodEnabled = true;
    /**
     * Использовать ли поиск по эквивалентным классам
     */
    private boolean equalsClassesMethodEnabled = false;

    public boolean isNearestNeighboursMethodEnabled() {
        return nearestNeighboursMethodEnabled;
    }

    public void setNearestNeighboursMethodEnabled(boolean nearestNeighboursMethodEnabled) {
        this.nearestNeighboursMethodEnabled = nearestNeighboursMethodEnabled;
    }

    public boolean isEqualsClassesMethodEnabled() {
        return equalsClassesMethodEnabled;
    }

    public void setEqualsClassesMethodEnabled(boolean equalsClassesMethodEnabled) {
        this.equalsClassesMethodEnabled = equalsClassesMethodEnabled;
    }

    public int getProximity() {
        return proximity;
    }

    public void setProximity(int proximity) {
        this.proximity = proximity;
    }
}
