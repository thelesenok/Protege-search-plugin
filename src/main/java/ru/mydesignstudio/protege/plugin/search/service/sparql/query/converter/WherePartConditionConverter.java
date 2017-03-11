package ru.mydesignstudio.protege.plugin.search.service.sparql.query.converter;

import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Created by abarmin on 05.02.17.
 */
public interface WherePartConditionConverter<T> {
    String convert(WherePart wherePart, T value, String variableName);
}
