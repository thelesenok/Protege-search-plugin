package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 04.05.17.
 *
 * Параметры поиска с учетом таксономической близости
 */
public class TaxonomyCollectorParams implements SearchProcessorParams {
    private int proximity = 3;

    public int getProximity() {
        return proximity;
    }

    public void setProximity(int proximity) {
        this.proximity = proximity;
    }
}
