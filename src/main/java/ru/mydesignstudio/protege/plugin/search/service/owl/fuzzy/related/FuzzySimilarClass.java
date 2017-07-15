package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.related;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;

/**
 * Created by abarmin on 25.06.17.
 *
 * Нечеткий похожий связанный класс
 */
public class FuzzySimilarClass extends OWLDomainClass {
    /**
     * Степень схожести
     */
    private final double weight;

    public FuzzySimilarClass(OWLClass owlClass, double weight) {
        super(owlClass);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
