package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator;

import org.semanticweb.owlapi.model.OWLDatatype;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

import java.util.Map;

/**
 * Created by abarmin on 13.05.17.
 *
 * Выбиралка подходящего типа данных по максимальному
 * значению функции принадлежности
 */
public class MaximumDatatypeCalculator implements DatatypeCalculator {
    @Override
    public OWLDatatype calculate(Map<OWLDatatype, Double> values) throws ApplicationException {
        double value = 0;
        OWLDatatype datatype = null;
        for (Map.Entry<OWLDatatype, Double> entry : values.entrySet()) {
            if (entry.getValue() > value) {
                value = entry.getValue();
                datatype = entry.getKey();
            }
        }
        return datatype;
    }
}
