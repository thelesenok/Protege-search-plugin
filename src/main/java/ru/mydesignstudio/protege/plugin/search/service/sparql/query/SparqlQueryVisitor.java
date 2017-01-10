package ru.mydesignstudio.protege.plugin.search.service.sparql.query;

import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.ui.model.OWLUIObject;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlQueryVisitor implements FromTypeVisitor, SelectQueryVisitor, WherePartVisitor {
    private static final String DEFAULTS =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
    private static final String PREFIX = "prfx";
    private static final String OBJECT = "object";
    private static final AtomicInteger variableIndex = new AtomicInteger(0);
    private static final String NEW_LINE = "\n";

    private String getVariableName() {
        return "?variable" + variableIndex.incrementAndGet();
    }

    @Override
    public String visit(FromType fromType) {
        final StringBuilder builder = new StringBuilder();
        builder.append("?")
                .append(OBJECT)
                .append(" a ")
                .append(fromType.getOwlClass().getIRI().toQuotedString())
                .append(". ");
        return builder.toString();
    }

    @Override
    public String visit(WherePart wherePart) {
        final StringBuilder builder = new StringBuilder();
        final String propertyName = wherePart.getProperty().getIRI().getFragment();
        final String variableName = getVariableName();
        // делаем выбор указанного критерия
        builder.append("?").append(OBJECT).append(" ");
        builder.append(PREFIX).append(":").append(propertyName).append(" ");
        builder.append(variableName).append(". ");
        // добавляем фильтр на указанный критерий
        final Object value = wherePart.getValue();
        final LogicalOperation operation = wherePart.getLogicalOperation();
        if (LogicalOperation.EQUALS.equals(operation)) {
            if (value instanceof OWLUIObject) {
                final OWLUIObject owluiObject = (OWLUIObject) value;
                builder.append("?")
                        .append(OBJECT)
                        .append(" ")
                        .append(PREFIX)
                        .append(":")
                        .append(wherePart.getProperty().getIRI().getFragment())
                        .append(" ")
                        .append(owluiObject.getQuotedString())
                        .append(".")
                        .append(NEW_LINE);
            } else {
                builder.append("FILTER(STR(" + variableName + ") = \"" + String.valueOf(value) + "\")");
            }
        } else if (LogicalOperation.LIKE.equals(operation)) {
            builder.append("FILTER(REGEX(" + variableName + ", \"" + String.valueOf(value) + "\", \"i\"))");
        } else {
            throw new RuntimeException("Not implemented yet");
        }
        return builder.toString();
    }

    @Override
    public String visit(SelectQuery selectQuery) {
        final StringBuilder builder = new StringBuilder();
        builder.append(DEFAULTS);
        builder.append("PREFIX ")
                .append(PREFIX)
                .append(": ")
                .append("<")
                .append(StringUtils.substringBefore(selectQuery.getFrom().getOwlClass().getIRI().toString(), "#"))
                .append("#")
                .append(">")
                .append(NEW_LINE);
        builder.append("SELECT * ");
        final List<WherePart> whereParts = selectQuery.getWhereParts();
        builder.append(" WHERE ");
        builder.append(" {");
        builder.append(visit(selectQuery.getFrom()));
        if (CollectionUtils.isNotEmpty(whereParts)) {
            for (WherePart wherePart : whereParts) {
                builder.append(
                        visit(
                                wherePart
                        )
                );
            }
        }
        builder.append(" }");
        return builder.toString();
    }
}
