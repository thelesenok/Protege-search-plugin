package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy;

/**
 * Created by abarmin on 26.06.17.
 *
 * Поддерживаемые типы аннотаций, в которых указана информация о нечетких объекта
 */
public enum FuzzyAnnotationType {
    /**
     * Содержит информацию о нечетких переменных и классах
     */
    CLASS_OR_DATATYPE("fuzzyLabel"),
    /**
     * Содержит информацию о весах атрибутов
     */
    PROPERTY("customLabel");

    private final String type;

    FuzzyAnnotationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
