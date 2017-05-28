package ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.WeighedRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.SparqlQueryVisitor;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.ProximityCalculatorFactory;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.proximity.calculator.ProximityCalculator;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;

/**
 * Created by abarmin on 28.05.17.
 *
 * Вспомогательные функции по вычислению веса строки
 */
public abstract class RowWeightCalculatorSupport implements WeighedRowWeightCalculator {
    private final SelectQuery selectQuery;
    private final OWLService owlService;
    private final ProximityCalculatorFactory calculatorFactory;
    private final SearchProcessorParams processorParams;

    public RowWeightCalculatorSupport(SelectQuery selectQuery, SearchProcessorParams params) {
        this.selectQuery = selectQuery;
        this.processorParams = params;
        //
        owlService = InjectionUtils.getInstance(OWLService.class);
        calculatorFactory = InjectionUtils.getInstance(ProximityCalculatorFactory.class);
    }

    @Override
    public double calculate(WeighedRow row) throws ApplicationException {
        /**
         * Здесь общая имплементация, которая берет каждое условие
         * в исходном запросе и проверяет соответствующее ему значение.
         * Затем вычисляет среднее значение
         */
        int paramsCount = 0;
        double weightTotal = 0;
        //
        final OWLIndividual ontologyObject = owlService.getIndividual((IRI) row.getCell(SparqlQueryVisitor.OBJECT));
        //
        for (WherePart wherePart : selectQuery.getWhereParts()) {
            final ProximityCalculator calculator = calculatorFactory.getCalculator(wherePart.getLogicalOperation(), processorParams);
            weightTotal += calculator.calculate(wherePart.getValue(), ontologyObject, wherePart.getProperty());
            paramsCount++;
        }
        //
        return paramsCount == 0 ? 1 : weightTotal / paramsCount;
    }
}
