package ru.mydesignstudio.protege.plugin.search.service.sparql.query.converter;

import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIIndividual;

/**
 * Created by abarmin on 05.02.17.
 */
public class IndividualWherePartConverter implements WherePartConditionConverter<OWLUIIndividual> {
    @Override
    public String convert(WherePart wherePart, OWLUIIndividual value, String variableName) {
        final LogicalOperation logicalOperation = wherePart.getLogicalOperation();
        if (!LogicalOperation.EQUALS.equals(logicalOperation)) {
            throw new RuntimeException("Only EQUALS operation for OWLIndividual is allowed");
        }
        final StringBuilder builder = new StringBuilder();
        final String stringValue = value.getNamedIndividual().getIRI().toURI().toString();
        builder.append("FILTER(STR(" + variableName + ") = \"" + stringValue + "\")");
        return builder.toString();
    }
}
