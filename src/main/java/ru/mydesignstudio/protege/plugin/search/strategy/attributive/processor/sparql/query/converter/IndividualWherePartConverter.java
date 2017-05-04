package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIIndividual;

/**
 * Created by abarmin on 05.02.17.
 */
public class IndividualWherePartConverter implements WherePartConditionConverter<OWLUIIndividual> {
    @Override
    public String convert(WherePart wherePart, OWLUIIndividual value, String variableName) throws ApplicationException {
        final LogicalOperation logicalOperation = wherePart.getLogicalOperation();
        if (!LogicalOperation.EQUALS.equals(logicalOperation)) {
            throw new ApplicationException("Only EQUALS operation for OWLIndividual is allowed");
        }
        final StringBuilder builder = new StringBuilder();
        final String stringValue = value.getNamedIndividual().getIRI().toURI().toString();
        builder.append("FILTER(STR(" + variableName + ") = \"" + stringValue + "\")");
        return builder.toString();
    }
}
