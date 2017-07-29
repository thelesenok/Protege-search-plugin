package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.weight.calculator;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.common.Validation;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.result.set.ResultSetRow;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.Weight;
import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.WeighedRowWeightCalculator;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.FuzzyOWLService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrappedCallback;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related.FuzzySimilarClass;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.InjectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abarmin on 25.06.17.
 */
public class FuzzyTaxonomyRowWeightCalculator implements WeighedRowWeightCalculator {
    /**
     * Множители весов - класс на множитель. Значения получаем из аннотации
     */
    private Map<OWLClass, Double> weightMultipliers = new HashMap<>();

    private final FuzzyOWLService fuzzyOWLService;
    private final ExceptionWrapperService wrapperService;

    public FuzzyTaxonomyRowWeightCalculator(OWLClass initialFromClass) {
        fuzzyOWLService = InjectionUtils.getInstance(FuzzyOWLService.class);
        wrapperService = InjectionUtils.getInstance(ExceptionWrapperService.class);
        //
        weightMultipliers = wrapperService.invokeWrapped(new ExceptionWrappedCallback<Map<OWLClass, Double>>() {
            @Override
            public Map<OWLClass, Double> run() throws ApplicationException {
                final Collection<FuzzySimilarClass> classes = fuzzyOWLService.getFuzzySimilarClasses(initialFromClass);
                final Map<OWLClass, Double> values = new HashMap<OWLClass, Double>();
                for (FuzzySimilarClass similarClass : classes) {
                    values.put(similarClass.getOwlClass(), similarClass.getWeight());
                }
                return values;
            }
        });
    }

    @Override
    public Weight calculate(ResultSetRow row) throws ApplicationException {
        /**
         * получим объект, которому соответствует текущая запись
         */
        final OWLIndividual individual = fuzzyOWLService.getIndividual(row.getObjectIRI());
        final Collection<OWLClass> individualClasses = fuzzyOWLService.getIndividualClasses(individual);
        /**
         * найдем из всей иерархии классов тот, у которого определен вес в аннотации
         */
        final OWLClass correspondingClass = CollectionUtils.findFirst(individualClasses, new Specification<OWLClass>() {
            @Override
            public boolean isSatisfied(OWLClass item) {
                return weightMultipliers.containsKey(item);
            }
        });
        Validation.assertNotNull(String.format(
                "There is no related class for %s",
                individual
        ), correspondingClass);
        /**
         * возвращаем вес
         */
        return new Weight(
                1,
                weightMultipliers.get(correspondingClass)
        );
    }
}
