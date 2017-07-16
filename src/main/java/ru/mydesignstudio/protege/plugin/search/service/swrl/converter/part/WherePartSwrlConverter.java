package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.common.Pair;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.service.PropertyBasedPathBuilder;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертируем отдельный {@link ru.mydesignstudio.protege.plugin.search.api.query.WherePart} в SWRL
 */
@Component
public class WherePartSwrlConverter implements CollectionItemSwrlConverter<Pair<OWLClass, WherePart>> {
    private final SwrlPrefixResolver prefixResolver;
    private final PropertyBasedPathBuilder pathBuilder;

    @Inject
    public WherePartSwrlConverter(SwrlPrefixResolver prefixResolver, PropertyBasedPathBuilder pathBuilder) {
        this.prefixResolver = prefixResolver;
        this.pathBuilder = pathBuilder;
    }

    @Override
    public String convert(Pair<OWLClass, WherePart> pair, int partNumber) throws ApplicationException {
        final Collection<String> parts = new ArrayList<>();
        final WherePart part = pair.getSecond();
        /**
         * Сконвертируем связывающую операцию
         */
        final LogicalOperation concatOperation = part.getConcatOperation();
        if (concatOperation != null) {
            parts.add(convertConcatOperation(concatOperation));
        }
        /**
         * Конвертируем промежуточные свойства
         */
        final Pair<String, Collection<String>> related = convertRelations(pair.getFirst(), pair.getSecond().getOwlClass(), partNumber);
        parts.addAll(related.getSecond());
        /**
         * Конвертируем свойство
         */
        parts.add(convertProperty(related.getFirst(), part.getProperty(), partNumber));
        /**
         * Добавляем оператор между свойством и условием
         */
        parts.add("^");
        /**
         * Конвертируем условие
         */
        parts.add(convertCondition(part.getLogicalOperation(), part.getValue(), partNumber));
        return StringUtils.join(parts, " ");
    }

    /**
     * Создать связки между классом From и классом свойства.
     * @param fromClass - от этого класса ищем путь
     * @param propertyClass - это класс свойства
     * @param partNumber - порядковый номер свойства
     * @return - Pair, где первое свойство - название переменной у которой будем на самом деле отбирать
     * @throws ApplicationException если нельзя найти путь между классами
     */
    private Pair<String, Collection<String>> convertRelations(OWLClass fromClass, OWLClass propertyClass, int partNumber) throws ApplicationException {
        final Collection<SelectField> path = pathBuilder.buildPath(fromClass, propertyClass);
        if (path.isEmpty()) {
            return new Pair<>(FieldConstants.OBJECT_IRI, Collections.emptyList());
        }
        final Collection<String> relations = new ArrayList<>();
        int relationIndex = 0;
        String lastVariable = FieldConstants.OBJECT_IRI;
        for (SelectField pathField : path) {
            final Pair<String, String> relation = convertRelation(pathField, partNumber, relationIndex);
            lastVariable = relation.getFirst();
            relations.add(relation.getSecond());
            relations.add("^");
            relationIndex++;
        }
        return new Pair<>(lastVariable, relations);
    }

    private Pair<String, String> convertRelation(SelectField pathField, int partNumber, int relationIndex) throws ApplicationException {
        final String relationVariable;
        final String sourceVariable;
        //
        if (relationIndex == 0) {
            relationVariable = "relation_0_" + partNumber;
            sourceVariable = FieldConstants.OBJECT_IRI;
        } else {
            relationVariable = "relation_" + relationIndex + "_" + partNumber;
            sourceVariable = "relation_" + (relationIndex - 1) + "_" + partNumber;
        }
        //
        final String propertyPrefix = prefixResolver.extractPrefix(pathField.getOwlClass().getIRI());
        final String propertyName = getPropertyName(pathField.getProperty());
        //
        final String relation = propertyPrefix + ":" + propertyName + "(?" + sourceVariable + ", ?" + relationVariable + ")";
        return new Pair<>(relationVariable, relation);
    }

    private String convertCondition(LogicalOperation logicalOperation, Object value, int partNumber) throws ApplicationException {
        return convertLogicalOperation(logicalOperation) + "(" +
                "?prop" + partNumber + ", " +
                convertValue(value) + ")";
    }

    /**
     * Конвертировать значение в swrl представление
     * @param value - значение
     * @return - строковое представление
     * @throws ApplicationException - в случае неподдерживаемого типа данных
     */
    private String convertValue(Object value) throws ApplicationException {
        if (value instanceof String) {
            return "\"" + value + "\"";
        } else if (value instanceof Number) {
            return value.toString();
        }
        throw new ApplicationException(String.format(
                "Unsupported value type %s",
                value.getClass()
        ));
    }

    /**
     * Конвертирует логические операции в swrl-представление
     * @param logicalOperation - логическая операция
     * @return - строковое представление
     * @throws ApplicationException - если операция не поддерживается
     */
    private String convertLogicalOperation(LogicalOperation logicalOperation) throws ApplicationException {
        switch (logicalOperation) {
            case LIKE: return "swrlb:matches";
            case EQUALS: return "swrlb:stringEqualIgnoreCase";
            case CONTAINS: return "swrlb:contains";
            case EQUALS_NOT: return "swrlb:notEqual";
            case STARTS_WITH: return "swrlb:startsWith";
            case ENDS_WITH: return "swrlb:endsWith";
            case MORE_THAN: return "swrlb:greaterThan";
            case MORE_OR_EQUALS: return "swrlb:greaterThanOrEqual";
            case LESS_THAN: return "swrlb:lessThan";
            case LESS_OR_EQUALS: return "swrlb:lessThanOrEqual";
            default: throw new ApplicationException(String.format(
                    "Unsupported logical operation %s",
                    logicalOperation
            ));
        }
    }

    /**
     * Конвертируем свойство в swrl представление
     * @param propertyOwnerVariable - в какой переменной живет ссылка на указанное свойство
     * @param property - свойство
     * @param partNumber - порядковый номер свойства
     * @return - строковое представление
     * @throws ApplicationException - если не может вычислить префикс
     */
    private String convertProperty(String propertyOwnerVariable, OWLProperty property, int partNumber) throws ApplicationException {
        final Collection<String> parts = new ArrayList<>();
        parts.add(prefixResolver.extractPrefix(property.getIRI()));
        parts.add(":");
        parts.add(getPropertyName(property));
        parts.add("(?" + propertyOwnerVariable + ", ?prop" + partNumber + ")");
        //
        return StringUtils.join(parts, "");
    }

    /**
     * Строковое название свойства
     * @param property - свойство
     * @return
     */
    private String getPropertyName(OWLProperty property) {
        return property.getIRI().getFragment();
    }

    /**
     * Конвертировать объединяющую операцию
     * @param concatOperation - объединяющая логическая операция
     * @return - строковое представление
     * @throws ApplicationException - если не может быть сконвертировано
     */
    private String convertConcatOperation(LogicalOperation concatOperation) throws ApplicationException {
        if (LogicalOperation.AND.equals(concatOperation)) {
            return "^";
        }
        throw new ApplicationException(String.format(
                "%s is not supported concat operation",
                concatOperation
        ));
    }
}
