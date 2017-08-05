package ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.weight.calculator;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.annotation.Component;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.service.owl.hierarchy.OwlClassHierarchyBuilder;
import ru.mydesignstudio.protege.plugin.search.strategy.taxonomy.processor.TaxonomyProcessorParams;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Created by abarmin on 28.05.17.
 *
 * Вычисляет вес строки с учетом таксономической близости
 */
@Component
public class TaxonomyRowWeightCalculator implements WeighedRowWeightCalculator {
	private final OWLService owlService;
	private final OwlClassHierarchyBuilder hierarchyBuilder;
    private TaxonomyProcessorParams processorParams;
    private OWLClass targetClass;

    @Inject
    public TaxonomyRowWeightCalculator(OWLService owlService, OwlClassHierarchyBuilder hierarchyBuilder) {
		this.owlService = owlService;
		this.hierarchyBuilder = hierarchyBuilder;
	}

	@Override
    public Weight calculate(ResultSetRow row) throws ApplicationException {
        final OWLIndividual ontologyObject = owlService.getIndividual(row.getObjectIRI());
        final OWLClass individualClass = owlService.getIndividualClass(ontologyObject);
        if (OWLUtils.equals(individualClass, getTargetClass())) {
            return Weight.maxWeight(1);
        }
        final Collection<OWLClass> hierarchy = hierarchyBuilder.build(individualClass);
        double doubleValue = (double) getProcessorParams().getProximity() / hierarchy.size();
        if (doubleValue > 1) {
            doubleValue = 1;
        }
        return new Weight(doubleValue, 1);
    }

    public OWLClass getTargetClass() {
        Validation.assertNotNull("Target class is not set", targetClass);
        return targetClass;
    }

    public void setTargetClass(OWLClass targetClass) {
        this.targetClass = targetClass;
    }

    public TaxonomyProcessorParams getProcessorParams() {
		Validation.assertNotNull("Processor params is not set", processorParams);
		return processorParams;
	}

	public void setProcessorParams(TaxonomyProcessorParams processorParams) {
		this.processorParams = processorParams;
	}
}
