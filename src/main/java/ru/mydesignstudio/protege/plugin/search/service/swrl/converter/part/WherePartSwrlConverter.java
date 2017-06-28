package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.LogicalOperation;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by abarmin on 26.06.17.
 *
 * Конвертируем отдельный {@link ru.mydesignstudio.protege.plugin.search.api.query.WherePart} в SWRL
 */
@Component
public class WherePartSwrlConverter implements CollectionItemSwrlConverter<WherePart> {
    private final SwrlPrefixResolver prefixResolver;

    @Inject
    public WherePartSwrlConverter(SwrlPrefixResolver prefixResolver) {
        this.prefixResolver = prefixResolver;
    }

    @Override
    public String convert(WherePart part, int partNumber) throws ApplicationException {
        final Collection<String> parts = new ArrayList<>();
        /**
         * Сконвертируем связывающую операцию
         */
        final LogicalOperation concatOperation = part.getConcatOperation();
        if (concatOperation != null) {
            parts.add(convertConcatOperation(concatOperation));
        }
        /**
         * Конвертируем свойство
         */
        parts.add(convertProperty(part.getProperty(), partNumber));
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
     * @param property - свойство
     * @param partNumber - порядковый номер свойства
     * @return - строковое представление
     * @throws ApplicationException - если не может вычислить префикс
     */
    private String convertProperty(OWLProperty property, int partNumber) throws ApplicationException {
        final Collection<String> parts = new ArrayList<>();
        parts.add(prefixResolver.extractPrefix(property.getIRI()));
        parts.add(":");
        parts.add(property.getIRI().getFragment());
        parts.add("(?object, ?prop" + partNumber + ")");
        //
        return StringUtils.join(parts, "");
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
