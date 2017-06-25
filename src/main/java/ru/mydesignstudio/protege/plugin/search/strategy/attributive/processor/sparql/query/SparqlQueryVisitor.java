package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.common.Triplet;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter.WherePartConditionConverter;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter.WherePartConditionConverterFactory;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by abarmin on 07.01.17.
 */
public class SparqlQueryVisitor implements FromTypeVisitor, SelectQueryVisitor, WherePartVisitor, ConcatOperationVisitor {
    private static final String DEFAULTS =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
    private static final String PREFIX = "prfx";
    private static final String OBJECT = "object";
    private static final AtomicInteger variableIndex = new AtomicInteger(0);
    private static final String NEW_LINE = "\n";
    private final Map<String, String> prefixes = new HashMap<String, String>();

    @Inject
    private OWLService owlService;
    @Inject
    private WherePartConditionConverterFactory conditionConverterFactory;

    private String getNextVariableName() {
        return "?variable" + variableIndex.incrementAndGet();
    }

    private String getCurrentVariableName() {
        return "?variable" + variableIndex.get();
    }

    @Override
    public String visit(FromType fromType) {
        final StringBuilder builder = new StringBuilder();
        builder.append("?")
                .append(OBJECT)
                .append(" a ")
                .append(fromType.getOwlClass().getIRI().toQuotedString())
                .append(". ")
                .append(NEW_LINE);
        return builder.toString();
    }

    private String createRelationalQuery(String variableName, OWLClass fromType, OWLClass targetType) throws ApplicationException {
        final StringBuilder builder = new StringBuilder();
        // добавляем класс, с которым связываем
        builder.append(variableName).append(" ")
                .append("a")
                .append(" ")
                .append(targetType.getIRI().toQuotedString())
                .append(".")
                .append(" ")
                .append(NEW_LINE);
        // теперь надо угадать свойство в исходном классе, которое отвечает
        // за связь с указанным классом
        for (OWLObjectProperty property : owlService.getObjectProperties(fromType)) {
            for (OWLPropertyRange range : owlService.getPropertyRanges(property)) {
                if (range.getClassesInSignature().contains(targetType)) {
                    builder.append("?")
                            .append(OBJECT)
                            .append(" ")
                            .append(getIRIPrefix(targetType.getIRI()))
                            .append(":")
                            .append(property.getIRI().getFragment())
                            .append(" ")
                            .append(variableName)
                            .append(". ")
                            .append(NEW_LINE);
                }
            }
        }
        return builder.toString();
    }

    private boolean isSameClasses(FromType fromType, SelectField wherePart) {
        return fromType.getOwlClass().equals(wherePart.getOwlClass());
    }

    @Override
    public Triplet<String, String, LogicalOperation> visit(FromType fromType, SelectField selectField) throws ApplicationException {
        final StringBuilder builder = new StringBuilder();
        final String propertyName = selectField.getProperty().getIRI().getFragment();
        final String variableName;
        final String leftVariableName;
        // если критерий не принадлежит целевому типу, то нужно добавить связь
        // костылябры, переписать
        if (!isSameClasses(fromType, selectField)) {
            builder.append(createRelationalQuery(getNextVariableName(), fromType.getOwlClass(), selectField.getOwlClass()));
            leftVariableName = getCurrentVariableName();
            variableName = getNextVariableName();
        } else {
            leftVariableName = "?" + OBJECT;
            variableName = getNextVariableName();
        }
        // делаем выбор указанного критерия
        builder.append(leftVariableName).append(" ");
        builder.append(getIRIPrefix(selectField.getProperty().getIRI()))
                .append(":")
                .append(propertyName)
                .append(" ")
                .append(variableName)
                .append(". ");
        // добавляем фильтр на указанный критерий
        final String condition;
        final LogicalOperation concatOperation;
        if (selectField instanceof WherePart) {
            final WherePart wherePart = (WherePart) selectField;
            final Object value = wherePart.getValue();
            //
            final Collection<OWLPropertyRange> ranges = owlService.getPropertyRanges(selectField.getProperty());
            final WherePartConditionConverter conditionConverter = conditionConverterFactory.getConverter(ranges, wherePart);
            condition = conditionConverter.convert(wherePart, value, variableName);
            concatOperation = wherePart.getConcatOperation();
        } else {
            condition = null;
            concatOperation = null;
        }
        //
        return new Triplet<>(
                builder.toString(),
                condition,
                concatOperation
        );
    }

    private void generatePrefixes(SelectQuery selectQuery) {
        int prefixIndex = 0;
        final IRI fromIRI = selectQuery.getFrom().getOwlClass().getIRI();
        prefixes.put(getIRINamespace(fromIRI), PREFIX + prefixIndex++);
        for (WherePart wherePart : selectQuery.getWhereParts()) {
            final IRI propertyIRI = wherePart.getProperty().getIRI();
            prefixes.put(getIRINamespace(propertyIRI), PREFIX + prefixIndex++);
        }
        for (SelectField selectField : selectQuery.getSelectFields()) {
            final IRI fieldIRI = selectField.getProperty().getIRI();
            prefixes.put(getIRINamespace(fieldIRI), PREFIX + prefixIndex++);
        }
    }

    private String getIRINamespace(IRI iri) {
        final URI uri = iri.toURI();
        return uri.getScheme() + ":" + uri.getSchemeSpecificPart();
    }

    private String getIRIPrefix(IRI iri) {
        final String namespace = getIRINamespace(iri);
        return prefixes.get(namespace);
    }

    @Override
    public String visit(SelectQuery selectQuery) throws ApplicationException {
        generatePrefixes(selectQuery);
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(DEFAULTS);
        for (Map.Entry<String, String> prefix : prefixes.entrySet()) {
            builder.append("PREFIX ")
                    .append(prefix.getValue())
                    .append(": ")
                    .append("<")
                    .append(prefix.getKey())
                    .append("#>")
                    .append(NEW_LINE);
        }
        builder.append("SELECT * ");
        final List<WherePart> whereParts = selectQuery.getWhereParts();
        builder.append(" WHERE ").append(NEW_LINE);
        builder.append(" {").append(NEW_LINE);
        builder.append(visit(selectQuery.getFrom()));
        if (CollectionUtils.isNotEmpty(selectQuery.getSelectFields())) {
            for (SelectField selectField : selectQuery.getSelectFields()) {
                final Triplet<String, String, LogicalOperation> fieldVisit = visit(selectQuery.getFrom(), selectField);
                builder.append(
                        fieldVisit.getFirst()
                );
            }
        }
        /**
         * WherePart-ы надо обходить в два захода:
         * 1. Добавляем только поля
         * 2. Добавляем на всех один FILTER через concatOperation
         */
        if (CollectionUtils.isNotEmpty(whereParts)) {
            final Collection<Triplet> wherePartVisits = new ArrayList<>();
            /**
             * Соберем все в одну коллекцию
             */
            for (WherePart wherePart : whereParts) {
                wherePartVisits.add(visit(selectQuery.getFrom(), wherePart));
            }
            /**
             * Добавляем поля
             */
            for (Triplet visit : wherePartVisits) {
                builder.append(visit.getFirst());
                builder.append(NEW_LINE);
            }
            /**
             * Добавляем условия
             */
            builder.append("FILTER(");
            for (Triplet<String, String, LogicalOperation> visit : wherePartVisits) {
                if (visit.getThird() != null) {
                    builder.append(
                            visit(visit.getThird())
                    );
                }
                builder.append(visit.getSecond());
            }
            builder.append(")");
            builder.append(NEW_LINE);
        }
        builder.append(" }");
        return builder.toString();
    }

    @Override
    public String visit(LogicalOperation concatOperation) throws ApplicationException {
        if (!LogicalOperation.getConcatOperations().contains(concatOperation)) {
            throw new ApplicationException(String.format(
                    "Operation %s is not a concat operation",
                    concatOperation
            ));
        }
        if (LogicalOperation.AND.equals(concatOperation)) {
            return " && ";
        }
        return " || ";
    }
}
