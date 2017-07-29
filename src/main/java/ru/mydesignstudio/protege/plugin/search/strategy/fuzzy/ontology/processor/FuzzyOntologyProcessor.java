package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.FieldConstants;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.DatatypeCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.weight.calculator.FuzzyOntologyRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 13.05.17.
 *
 * Процессор для поиска с учетом лингвистических переменных
 */
@Component
public class FuzzyOntologyProcessor implements SearchProcessor<FuzzyOntologyProcessorParams> {
    private final OWLService owlService;
    private final FuzzyOWLService fuzzyOWLService;
    private final DatatypeCalculator datatypeCalculator;
    private final ExceptionWrapperService wrapperService;

    private Collection<WherePart> fuzzyConditions = new ArrayList<>();

    @Inject
    public FuzzyOntologyProcessor(OWLService owlService, FuzzyOWLService fuzzyOWLService,
                                  DatatypeCalculator datatypeCalculator, ExceptionWrapperService wrapperService) {
        this.owlService = owlService;
        this.fuzzyOWLService = fuzzyOWLService;
        this.datatypeCalculator = datatypeCalculator;
        this.wrapperService = wrapperService;
    }

    @Override
    public SelectQuery prepareQuery(SelectQuery initialQuery, FuzzyOntologyProcessorParams strategyParams) throws ApplicationException {
        /**
         * Оставим только обычные условия, fuzzy соберем в отдельную коллекцию
         * и будем проверять уже после основного поиска
         */
        fuzzyConditions = new ArrayList<>();
        final Collection<WherePart> defaultConditions = new ArrayList<>();
        for (WherePart wherePart : initialQuery.getWhereParts()) {
            final Collection<OWLPropertyRange> ranges = owlService.getPropertyRanges(wherePart.getProperty());
            if (LogicalOperationHelper.hasFuzzyExpression(ranges)) {
                fuzzyConditions.add(wherePart);
            } else {
                defaultConditions.add(wherePart);
            }
        }
        initialQuery.emptyWhereParts();
        for (WherePart wherePart : defaultConditions) {
            initialQuery.addWherePart(wherePart);
        }
        /**
         * Нечеткие поля добавляем просто как отбираемые поля
         */
        for (WherePart condition : fuzzyConditions) {
            initialQuery.addSelectField(new SelectField(
                    condition.getOwlClass(),
                    condition.getProperty()
            ));
        }
        return initialQuery;
    }

    @Override
    public ResultSet collect(ResultSet initialResultSet, SelectQuery selectQuery, FuzzyOntologyProcessorParams strategyParams) throws ApplicationException {
        /**
         * если нечетких свойств нет, возвращаем все как есть
         */
        if (CollectionUtils.isEmpty(fuzzyConditions)) {
            return initialResultSet;
        }
        /**
         * нечеткие свойства есть
         */
        final Map<OWLProperty, OWLDatatype> values = new HashMap<>();
        for (WherePart fuzzyWherePart : fuzzyConditions) {
            /**
             * вычислим, к какому типу данных должно относиться значение
             */
            final OWLDatatype datatype = calculateTargetDatatype(fuzzyWherePart.getProperty(), fuzzyWherePart.getValue());
            values.put(fuzzyWherePart.getProperty(), datatype);
        }
        /**
         * пройдем по всем записям из initialResultSet и уберем оттуда все, которые не подходят по условиям
         * нечеткого поиска
         */
        for (ResultSetRow sourceRow : initialResultSet.getRows()) {
            if (!isValidFuzzyRow(sourceRow, selectQuery, values)) {
                initialResultSet.removeRow(sourceRow);
            }
        }
        /**
         * а теперь делаем из этого обычный взвешенный набор данных
         */
        return new WeighedResultSet(initialResultSet, getWeightCalculator(selectQuery, strategyParams, fuzzyConditions));
    }

    protected boolean isValidFuzzyRow(ResultSetRow row, SelectQuery fuzzyQuery, Map<OWLProperty, OWLDatatype> fuzzyValues) throws ApplicationException {
        final IRI recordIRI = (IRI) row.getValue(FieldConstants.OBJECT_IRI);
        final OWLIndividual record = owlService.getIndividual(recordIRI);
        return isValidFuzzyRow(
                fuzzyQuery.getFrom().getOwlClass(),
                record,
                fuzzyValues
        );
    }

    /**
     * Подходит ли запись из результатов
     * @param recordClass - класс записи, который отбираем
     * @param record - запись, которую проверяем
     * @param values - набор нечетких свойств
     * @return - признак того, что запись подходит
     * @throws ApplicationException
     */
    private boolean isValidFuzzyRow(OWLClass recordClass, OWLIndividual record, Map<OWLProperty, OWLDatatype> values) throws ApplicationException {
        /**
         * пройдем по всем свойствам, если свойство есть в fuzzyValues и тип совпадает,
         * то все подходит
         */
        final Collection<OWLDataProperty> properties = owlService.getDataProperties(recordClass);
        return CollectionUtils.every(values.entrySet(), new Specification<Map.Entry<OWLProperty, OWLDatatype>>() {
            @Override
            public boolean isSatisfied(Map.Entry<OWLProperty, OWLDatatype> entry) {
                final OWLProperty property = entry.getKey();
                final OWLDatatype fuzzyType = entry.getValue();
                if (fuzzyType == null) {
                    return false;
                }
                if (properties.contains(property)) {
                    final OWLDatatype propertyType = wrapperService.invokeWrapped(new ExceptionWrappedCallback<OWLDatatype>() {
                        @Override
                        public OWLDatatype run() throws ApplicationException {
                            return fuzzyOWLService.getPropertyDatatype(record, property);
                        }
                    });
                    return fuzzyType.equals(propertyType);
                }
                return false;
            }
        });
    }

    /**
     * Объект для вычисления степени схожести строки
     * @param selectQuery - запрос, по которому получен экземпляр
     * @param strategyParams - параметры процессора
     * @param fuzzyParts - нечеткие параметры из запрос (из selectQuery эти параметры убраны)
     * @return
     */
    private WeighedRowWeightCalculator getWeightCalculator(SelectQuery selectQuery, FuzzyOntologyProcessorParams strategyParams, Collection<WherePart> fuzzyParts) {
        return new FuzzyOntologyRowWeightCalculator(selectQuery, strategyParams, fuzzyParts);
    }

    /**
     * Вычисляем лингвистическое значение свойства property, которое соответствует переданному value
     * @param property - свойство, которое содержит лингвистические значения
     * @param value - численное значение свойства
     * @return - тип, который соответствует лингвистическому значение
     */
    private OWLDatatype calculateTargetDatatype(OWLProperty property, Object value) throws ApplicationException {
        int intValue = Integer.parseInt((String) value);
        /**
         * здесь нам точно известно, что свойство нечеткое, поэтому берем акиомы по этому свойству
         * ranges - набор OWLDatatype, у них нужно взять аннотацию, в ней в формате XML описана
         * функция принадлежности
         */
        final Collection<OWLPropertyRange> ranges = owlService.getPropertyRanges(property);
        final Map<OWLDatatype, FuzzyFunction> functions = new HashMap<>();
        for (OWLPropertyRange range : ranges) {
            final OWLDatatype datatype = (OWLDatatype) range;
            functions.put(datatype, fuzzyOWLService.getFuzzyFunction(datatype));
        }
        /**
         * Вычислим значения функций принадлежностей для лингвистических
         * переменных
         */
        final Map<OWLDatatype, Double> functionValues = new HashMap<>();
        for (Map.Entry<OWLDatatype, FuzzyFunction> entry : functions.entrySet()) {
            functionValues.put(
                    entry.getKey(),
                    entry.getValue().evaluate(intValue)
            );
        }
        /**
         * Теперь нужно на основе значений принадлежности выбрать
         * подходящий тип данных.
         */
        return datatypeCalculator.calculate(functionValues);
    }
}
