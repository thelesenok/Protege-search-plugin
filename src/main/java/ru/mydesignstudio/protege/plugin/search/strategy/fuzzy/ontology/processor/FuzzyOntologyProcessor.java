package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.reasoner.sparql.result.set.SparqlResultSet;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.DatatypeCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abarmin on 13.05.17.
 *
 * Процессор для поиска с учетом лингвистических переменных
 */
public class FuzzyOntologyProcessor implements SearchProcessor<FuzzyOntologyProcessorParams> {
    @Inject
    private OWLService owlService;
    @Inject
    private FuzzyOWLService fuzzyOWLService;
    @Inject
    private DatatypeCalculator datatypeCalculator;
    @Inject
    private ExceptionWrapperService wrapperService;

    private Collection<WherePart> fuzzyConditions = new ArrayList<>();

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
         * сделаем проход поиска с учетом вычисленных типов данных
         */
        final SparqlResultSet resultSet = new SparqlResultSet(getColumnNames(initialResultSet));
        for (int rowIndex = 0; rowIndex < initialResultSet.getRowCount(); rowIndex++) {
            // TODO: 13.05.17 не очень надежный способ, не факт, что в первом столбце запись
            final IRI recordIRI = (IRI) initialResultSet.getResult(rowIndex, 0);
            final OWLIndividual record = owlService.getIndividual(recordIRI);
            if (isValidRow(selectQuery.getFrom().getOwlClass(), record, values)) {
                addRow(resultSet, initialResultSet, rowIndex);
            }
        }
        return resultSet;
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

    /**
     * Подходит ли запись из результатов
     * @param record - запись, которую проверяем
     * @param values - набор нечетких свойств
     * @return
     * @throws ApplicationException
     */
    private boolean isValidRow(OWLClass recordClass, OWLIndividual record, Map<OWLProperty, OWLDatatype> values) throws ApplicationException {
        /**
         * пройдем по всем свойствам, если свойство есть в values и тип совпадает,
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
     * Скопировать строку из исходного сета в целевой
     * @param resultSet - в этот копируем
     * @param initialResultSet - из этого копируем
     * @param rowIndex - это номер строки для копирования
     */
    private void addRow(SparqlResultSet resultSet, ResultSet initialResultSet, int rowIndex) {
        final List<Object> row = new ArrayList<>();
        for (int colIndex = 0; colIndex < initialResultSet.getColumnCount(); colIndex++) {
            row.add(initialResultSet.getResult(rowIndex, colIndex));
        }
        resultSet.addRow(row);
    }

    /**
     * Получить названия столбцов
     * @param initialResultSet - исходный resultSet
     * @return
     */
    private List<String> getColumnNames(ResultSet initialResultSet) {
        final List<String> names = new ArrayList<>();
        for (int index = 0; index < initialResultSet.getColumnCount(); index++) {
            names.add(initialResultSet.getColumnName(index));
        }
        return names;
    }
}
