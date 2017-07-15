package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.xml;

/**
 * Created by abarmin on 26.06.17.
 *
 * Тип значения в аннотации
 */
public enum CustomLabelType {
    /**
     * Вес атрибута
     */
    PRIORITY("Priority");

    private final String type;

    CustomLabelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
