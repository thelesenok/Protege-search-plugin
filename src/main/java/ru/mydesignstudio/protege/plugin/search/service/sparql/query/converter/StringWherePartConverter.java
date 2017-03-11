package ru.mydesignstudio.protege.plugin.search.service.sparql.query.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Created by abarmin on 05.02.17.
 */
public class StringWherePartConverter implements WherePartConditionConverter<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringWherePartConverter.class);

    @Override
    public String convert(WherePart wherePart, String value, String variableName) {
        final LogicalOperation operation = wherePart.getLogicalOperation();
        final StringBuilder builder = new StringBuilder();
        builder.append("FILTER(");
        if (LogicalOperation.EQUALS.equals(operation)) {
            builder.append("STR(" + variableName + ") = \"" + value + "\"");
        } else if (LogicalOperation.EQUALS_NOT.equals(operation)) {
            builder.append("STR(" + variableName + ") != \"" + value + "\"");
        } else if (LogicalOperation.LIKE.equals(operation)) {
            builder.append("REGEX(" + variableName + ", \"" + value + "\", \"i\")");
        } else if (LogicalOperation.STARTS_WITH.equals(operation)) {
            builder.append("REGEX(" + variableName + ", \"^" + value + "\", \"i\")");
        } else if (LogicalOperation.ENDS_WITH.equals(operation)) {
            builder.append("REGEX(" + variableName + ", \"" + value + "$\", \"i\")");
        } else {
            LOGGER.error("Unsupported operation {}", operation);
            throw new RuntimeException(String.format(
                    "Unsupported operation %s",
                    operation
            ));
        }
        builder.append(")");
        return builder.toString();
    }
}
