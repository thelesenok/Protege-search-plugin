package ru.mydesignstudio.protege.plugin.search.domain;

import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.utils.OWLUtils;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

/**
 * Created by abarmin on 04.01.17.
 *
 * Класс предметной области из онтологии
 */
public class OWLDomainClass implements OWLDomainObject {
    /**
     * Какой класс онтологии здесь обернут
     */
    private final OWLClass owlClass;

    public OWLDomainClass(OWLClass owlClass) {
        this.owlClass = owlClass;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    /**
     * Является ли класс вершиной иерархии (Thing)
     * @return
     */
    public boolean isTopLevelClass() {
        return StringUtils.equalsIgnoreCase(
                getOwlClass().getIRI().getFragment(),
                "Thing"
        );
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OWLDomainClass that = (OWLDomainClass) o;

        return OWLUtils.equals(this, that);
    }

    @Override
    public int hashCode() {
        return owlClass.hashCode();
    }
}
