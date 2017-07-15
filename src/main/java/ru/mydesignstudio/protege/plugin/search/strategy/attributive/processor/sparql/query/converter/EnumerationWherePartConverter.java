package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainLiteral;

/**
 * Created by abarmin on 06.05.17.
 *
 * Конвертер значений в sparql из перечисления
 */
public class EnumerationWherePartConverter implements WherePartConditionConverter<OWLDomainLiteral> {
    @Override
    public String convert(WherePart wherePart, OWLDomainLiteral value, String variableName) throws ApplicationException {
        final LogicalOperation logicalOperation = wherePart.getLogicalOperation();
        if (!LogicalOperation.EQUALS.equals(logicalOperation)) {
            throw new ApplicationException("Only EQUALS operation for OWLLiteral is allowed");
        }
        final StringBuilder builder = new StringBuilder();
        final String stringValue = value.getLiteral().getLiteral();
        builder.append("(STR(" + variableName + ") = \"" + stringValue + "\")");
        return builder.toString();
    }
}
