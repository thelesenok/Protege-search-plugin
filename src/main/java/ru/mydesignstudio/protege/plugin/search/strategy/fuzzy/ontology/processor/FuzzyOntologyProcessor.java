package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor;

import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.processor.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator.DatatypeCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.result.set.FuzzyWeighedResultSet;
import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.weight.calculator.FuzzyOntologyRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.LogicalOperationHelper;

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
public class FuzzyOntologyProcessor implements SearchProcessor<FuzzyOntologyProcessorParams> {
    @Inject
    private OWLService owlService;
    @Inject
    private FuzzyOWLService fuzzyOWLService;
    @Inject
    private DatatypeCalculator datatypeCalculator;

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
         * конвертируем в подходящий resultset
         */
        return new FuzzyWeighedResultSet(
                initialResultSet,
                getWeightCalculator(selectQuery, strategyParams, fuzzyConditions),
                selectQuery,
                values
        );
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
