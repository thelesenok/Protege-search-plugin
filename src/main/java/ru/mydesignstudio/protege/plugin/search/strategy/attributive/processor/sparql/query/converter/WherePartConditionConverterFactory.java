package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.processor.FuzzyWherePart;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by abarmin on 06.05.17.
 *
 * Фабрика конвертеров значений
 */
public class WherePartConditionConverterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WherePartConditionConverterFactory.class);

    @Inject
    private IndividualWherePartConverter individualConverter;
    @Inject
    private IntegerWherePartConverter integerConverter;
    @Inject
    private StringWherePartConverter stringConverter;
    @Inject
    private DateWherePartConverter dateConverter;
    @Inject
    private EnumerationWherePartConverter enumerationConverter;
    @Inject
    private FuzzyWherePartConverter fuzzyConverter;

    /**
     * Получить конвертер по набору возможных значений свойств
     * @param ranges - набор свойств
     * @param wherePart - условие для конвертации
     * @return - конвертер
     * @throws ApplicationException
     */
    public WherePartConditionConverter getConverter(Collection<OWLPropertyRange> ranges, WherePart wherePart) throws ApplicationException {
        if (wherePart instanceof FuzzyWherePart) {
            return fuzzyConverter;
        } else if (LogicalOperationHelper.hasClassExpression(ranges)) {
            return individualConverter;
        } else if (LogicalOperationHelper.hasIntegerExpression(ranges)) {
            return integerConverter;
        } else if (LogicalOperationHelper.hasStringExpression(ranges)) {
            return stringConverter;
        } else if (LogicalOperationHelper.hasDateExpression(ranges)) {
            return dateConverter;
        } else if (LogicalOperationHelper.hasEnumerationExpression(ranges)) {
            return enumerationConverter;
        } else {
            LOGGER.error("Can't get value converter");
            throw new ApplicationException("Can't get value converter");
        }
    }
}
