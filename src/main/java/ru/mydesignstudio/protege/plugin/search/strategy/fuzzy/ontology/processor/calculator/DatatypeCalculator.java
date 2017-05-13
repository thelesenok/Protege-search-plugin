package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.ontology.processor.calculator;

import org.semanticweb.owlapi.model.OWLDatatype;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

import java.util.Map;

/**
 * Created by abarmin on 13.05.17.
 *
 * Вычисяет наиболее подходящее значение лингвистической переменной
 */
public interface DatatypeCalculator {
    /**
     * Выбрать подходящий тип данных на основе переданных значений
     * @param values
     * @return
     * @throws ApplicationException
     */
    OWLDatatype calculate(Map<OWLDatatype, Double> values) throws ApplicationException;
}
