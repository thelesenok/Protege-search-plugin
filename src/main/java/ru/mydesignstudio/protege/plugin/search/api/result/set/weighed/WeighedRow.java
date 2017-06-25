package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed;

import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;

/**
 * Created by abarmin on 06.05.17.
 *
 * Строка взвешенных результатов
 */
public interface WeighedRow extends ResultSetRow {
    /**
     * Получить вес текущей строки
     * @return - вес строки
     */
    Weight getWeight();

    /**
     * Установить вес текущей строки
     * @param weight - вес
     */
    void addWeight(Weight weight);
}
