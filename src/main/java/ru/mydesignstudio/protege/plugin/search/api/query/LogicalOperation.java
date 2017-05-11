package ru.mydesignstudio.protege.plugin.search.api.query;

/**
 * Created by abarmin on 04.01.17.
 *
 * Логическая операция
 */
public enum LogicalOperation {
    EQUALS,
    LIKE,
    FUZZY_LIKE,
    CONTAINS,
    EQUALS_NOT,
    STARTS_WITH,
    ENDS_WITH,
    MORE_THAN,
    LESS_THAN,
    MORE_OR_EQUALS,
    LESS_OR_EQUALS;
}
