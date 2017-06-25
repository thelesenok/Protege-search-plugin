package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.weight.calculator;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator.RowWeightCalculatorSupport;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

import java.util.Collection;

/**
 * Created by abarmin on 28.05.17.
 *
 * Калькулятор веса строки для нечеткого поиска по онтологии
 */
public class FuzzyOntologyRowWeightCalculator extends RowWeightCalculatorSupport implements WeighedRowWeightCalculator {
    private final Collection<WherePart> fuzzyParts;
    private final FuzzyOWLService fuzzyOWLService;
    private final OWLService owlService;

    public FuzzyOntologyRowWeightCalculator(SelectQuery selectQuery, SearchProcessorParams params, Collection<WherePart> whereParts) {
        super(selectQuery, params);
        fuzzyParts = whereParts;
        //
        fuzzyOWLService = InjectionUtils.getInstance(FuzzyOWLService.class);
        owlService = InjectionUtils.getInstance(OWLService.class);
    }

    @Override
    public Weight calculate(ResultSetRow row) throws ApplicationException {
        /**
         * Сначала вычислим значение на основе атрибутов
         */
        final Weight byAttributes = super.calculate(row);
        /**
         * А теперь пройдем по нечетким критериям
         * Здесь нужно решить обратную задачу - насколько значение
         * из записи соответствует тому, что пользователь указал в форме ввода.
         * Т.е. в записи указано "подросток", диапазон 6-18, пользователь
         * указал 12. Вычисляем, насколько 12 подходит под значение "подросток".
         */
        for (WherePart fuzzyPart : fuzzyParts) {
            final OWLDatatype propertyDatatype = getPropertyDatatype(row, fuzzyPart.getProperty());
            final double fuzzyValue = getDatatypeFunctionValue(propertyDatatype, Integer.parseInt((String) fuzzyPart.getValue()));
            byAttributes.addWeight(new Weight(fuzzyValue, 1));
        }
        /**
         * Вычислением полного веса займется другая часть
         */
        return byAttributes;
    }

    private double getDatatypeFunctionValue(OWLDatatype propertyDatatype, int value) throws ApplicationException {
        final FuzzyFunction fuzzyFunction = fuzzyOWLService.getFuzzyFunction(propertyDatatype);
        return fuzzyFunction.evaluate(value);
    }

    /**
     * Тип данных, который указан у объекта в переданной строке в указанном свойстве
     * @param row - взвешенная строка
     * @param property - свойство
     * @return
     */
    private OWLDatatype getPropertyDatatype(ResultSetRow row, OWLProperty property) throws ApplicationException {
        final IRI iri = row.getObjectIRI();
        final OWLIndividual record = owlService.getIndividual(iri);
        return fuzzyOWLService.getPropertyDatatype(record, property);
    }
}
