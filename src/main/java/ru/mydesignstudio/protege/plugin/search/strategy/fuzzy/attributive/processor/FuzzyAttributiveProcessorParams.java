package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor;

import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;

/**
 * Created by abarmin on 11.05.17.
 *
 * Параметры нечеткого поиска
 */
public class FuzzyAttributiveProcessorParams implements SearchProcessorParams {
    /**
     * Количество символов в маске
     */
    private int maskSize = 2;

    public FuzzyAttributiveProcessorParams() {
    }

    public FuzzyAttributiveProcessorParams(int maskSize) {
        this.maskSize = maskSize;
    }

    public int getMaskSize() {
        return maskSize;
    }

    public void setMaskSize(int maskSize) {
        this.maskSize = maskSize;
    }
}
