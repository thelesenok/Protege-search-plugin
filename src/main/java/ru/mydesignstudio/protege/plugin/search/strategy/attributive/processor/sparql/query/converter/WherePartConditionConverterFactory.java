package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;

import java.util.Collection;

import javax.inject.Inject;

import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.attributive.processor.FuzzyWherePart;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;

/**
 * Created by abarmin on 06.05.17.
 *
 * Фабрика конвертеров значений
 */
@Component
public class WherePartConditionConverterFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WherePartConditionConverterFactory.class);

    private final IndividualWherePartConverter individualConverter;
    private final NumericWherePartConverter numericConverter;
    private final StringWherePartConverter stringConverter;
    private final DateWherePartConverter dateConverter;
    private final EnumerationWherePartConverter enumerationConverter;
    private final FuzzyWherePartConverter fuzzyConverter;
    private final BooleanWherePartConverter booleanConverter;

    @Inject
    public WherePartConditionConverterFactory(IndividualWherePartConverter individualConverter,
			NumericWherePartConverter numericConverter, StringWherePartConverter stringConverter,
			DateWherePartConverter dateConverter, EnumerationWherePartConverter enumerationConverter,
			FuzzyWherePartConverter fuzzyConverter, BooleanWherePartConverter booleanConverter) {
		this.individualConverter = individualConverter;
		this.numericConverter = numericConverter;
		this.stringConverter = stringConverter;
		this.dateConverter = dateConverter;
		this.enumerationConverter = enumerationConverter;
		this.fuzzyConverter = fuzzyConverter;
		this.booleanConverter = booleanConverter;
	}

	/**
     * Получить конвертер по набору возможных значений свойств
     * @param ranges - набор свойств
     * @param wherePart - условие для конвертации
     * @return - конвертер
     * @throws ApplicationException
     */
    @SuppressWarnings("rawtypes")
	public WherePartConditionConverter getConverter(Collection<OWLPropertyRange> ranges, WherePart wherePart) throws ApplicationException {
        if (wherePart instanceof FuzzyWherePart) {
            return fuzzyConverter;
        } else if (LogicalOperationHelper.hasClassExpression(ranges)) {
            return individualConverter;
        } else if (LogicalOperationHelper.hasNumericExpression(ranges)) {
            return numericConverter;
        } else if (LogicalOperationHelper.hasStringExpression(ranges)) {
            return stringConverter;
        } else if (LogicalOperationHelper.hasDateExpression(ranges)) {
            return dateConverter;
        } else if (LogicalOperationHelper.hasEnumerationExpression(ranges)) {
            return enumerationConverter;
        } else if (LogicalOperationHelper.hasBooleanExpression(ranges)) {
        		return booleanConverter;
        } else {
            LOGGER.error("Can't get value converter");
            throw new ApplicationException("Can't get value converter");
        }
    }
}
