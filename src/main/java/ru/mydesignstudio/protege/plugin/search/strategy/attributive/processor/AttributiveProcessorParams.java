package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 12.03.17.
 *
 * Параметры атрибутивного поиска
 */
public class AttributiveProcessorParams implements SearchProcessorParams {
    /**
     * Запрос для поиска
     */
    private final SelectQuery selectQuery;
    /**
     * При расчете веса записи использовать веса атрибутов
     */
    private final boolean useAttributeWeights;

    public AttributiveProcessorParams(SelectQuery selectQuery, boolean useAttributeWeights) {
        this.selectQuery = selectQuery;
        this.useAttributeWeights = useAttributeWeights;
    }

    public boolean isUseAttributeWeights() {
        return useAttributeWeights;
    }

    public SelectQuery getSelectQuery() {
        return selectQuery;
    }
}
