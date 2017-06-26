package ru.mydesignstudio.protege.plugin.search.strategy.support.weight.calculator;

import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculator;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorFactory;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.search.component.SearchProcessorParams;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
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
    public Weight calculate(ResultSetRow row) throws ApplicationException {
        /**
         * Здесь общая имплементация, которая берет каждое условие
         * в исходном запросе и проверяет соответствующее ему значение.
         * Затем вычисляет среднее значение
         */
        final Weight totalWeight = Weight.noneWeight();
        //
        final OWLIndividual ontologyObject = owlService.getIndividual(row.getObjectIRI());
        final boolean useAttributeWeights = (processorParams instanceof AttributiveProcessorParams) ?
                ((AttributiveProcessorParams) processorParams).isUseAttributeWeights() :
                false;
        //
        for (WherePart wherePart : selectQuery.getWhereParts()) {
            final ProximityCalculator calculator = calculatorFactory.getCalculator(wherePart.getLogicalOperation(), processorParams);
            final Weight partWeight = calculator.calculate(
                    wherePart.getValue(),
                    ontologyObject,
                    wherePart.getProperty(),
                    useAttributeWeights
            );
            totalWeight.addWeight(partWeight);
        }
        //
        return totalWeight;
    }
}
