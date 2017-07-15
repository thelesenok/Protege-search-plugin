package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;

/**
 * Converter for boolean values in where part
 * @author abarmin
 */
@Component
public class BooleanWherePartConverter implements WherePartConditionConverter<Boolean> {
	@Override
	public String convert(WherePart wherePart, Boolean value, String variableName) throws ApplicationException {
		final LogicalOperation logicalOperation = wherePart.getLogicalOperation();
		final StringBuilder builder = new StringBuilder();
		builder.append("(").append(variableName);
		if (LogicalOperation.EQUALS.equals(logicalOperation)) {
			builder.append("=");
		} else if (LogicalOperation.EQUALS_NOT.equals(logicalOperation)) {
			builder.append("!=");
		} else {
			throw new ApplicationException("Unsupported operation for boolean where part");
		}
		builder.append(value);
		builder.append(")");
		return builder.toString();
	}

}
