package ru.mydesignstudio.protege.plugin.search.domain;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.Collection;

/**
 * Created by abarmin on 04.01.17.
 *
 * Класс предметной области из онтологии
 */
public class OWLDomainClass implements OWLDomainObject {
    private final OWLClass owlClass;
    private OWLDomainClass parent;
    private Collection<OWLDomainClass> children;

    public OWLDomainClass(OWLClass owlClass) {
        this.owlClass = owlClass;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    @Override
    public String getQuotedString() {
        return getOwlClass().getIRI().toQuotedString();
    }

    @Override
    public String toString() {
        return StringUtils.substringAfter(
                owlClass.toStringID(),
                "#"
        );
    }
}
