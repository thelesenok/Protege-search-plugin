package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.weight.calculator;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;

import java.util.Collection;

/**
 * Created by abarmin on 28.05.17.
 *
 * Вычисляет вес строки с учетом таксономической близости
 */
public class TaxonomyRowWeightCalculator implements WeighedRowWeightCalculator {
    private final TaxonomyProcessorParams processorParams;
    private final OWLService owlService;

    public TaxonomyRowWeightCalculator(TaxonomyProcessorParams processorParams) {
        this.processorParams = processorParams;
        owlService = InjectionUtils.getInstance(OWLService.class);
    }

    @Override
    public Weight calculate(ResultSetRow row) throws ApplicationException {
        final OWLIndividual ontologyObject = owlService.getIndividual(row.getObjectIRI());
        final OWLClass individualClass = owlService.getIndividualClass(ontologyObject);
        final Collection<OWLDomainClass> hierarchy = OWLUtils.getClassesInHierarchy(new OWLDomainClass(individualClass));
        double doubleValue = (double) processorParams.getProximity() / (hierarchy.size() + 1);// добавляем еще и сам класс
        if (doubleValue > 1) {
            doubleValue = 1;
        }
        return new Weight(doubleValue, 1);
    }
}
