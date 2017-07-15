package ru.mydesignstudio.protege.plugin.search.api.query;

import java.util.Arrays;
import java.util.Collection;

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
    LESS_OR_EQUALS,
    AND,
    OR;

    /**
     * Операции, через которые могут быть объединены условия
     * @return
     */
    public static Collection<LogicalOperation> getConcatOperations() {
        return Arrays.asList(
                AND,
                OR
        );
    }
}
