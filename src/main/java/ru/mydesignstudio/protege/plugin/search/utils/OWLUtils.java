package ru.mydesignstudio.protege.plugin.search.utils;

import java.util.Collection;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainClass;

/**
 * Created by abarmin on 06.05.17.
 *
 * Служебные методы по работе с онтологией
 */
public class OWLUtils {
    /**
     * Есть ли у указанного класса указанное свойство
     * @param sharedClass - у этого класса ищем
     * @param property - это свойство ищем
     * @throws ApplicationException
     * @return
     */
    public static final boolean hasProperty(OWLDomainClass sharedClass, OWLProperty property) throws ApplicationException {
        final OWLService owlService = InjectionUtils.getInstance(OWLService.class);
        final Collection<OWLDataProperty> dataProperties = owlService.getDataProperties(sharedClass.getOwlClass());
        if (CollectionUtils.some(dataProperties, new Specification<OWLDataProperty>() {
            @Override
            public boolean isSatisfied(OWLDataProperty owlDataProperty) {
                return property.equals(owlDataProperty);
            }
        })) {
            return true;
        }
        final Collection<OWLObjectProperty> objectProperties = owlService.getObjectProperties(sharedClass.getOwlClass());
        return CollectionUtils.some(objectProperties, new Specification<OWLObjectProperty>() {
            @Override
            public boolean isSatisfied(OWLObjectProperty owlObjectProperty) {
                return property.equals(owlObjectProperty);
            }
        });
    }

    /**
     * Являются ли два класса эквивалентными
     * @param first - этот
     * @param second - и этот
     * @return
     */
    public static final boolean equals(OWLClass first, OWLClass second) {
        /**
         * Как оказалось, OWLClass.equals не работает корректно
         */
        return StringUtils.equalsIgnoreCase(
                first.getIRI().getFragment(),
                second.getIRI().getFragment()
        );
    }

    /**
     * Являются ли два класса эквивалентными
     * @param first - этот
     * @param second - и этот
     * @return
     */
    public static final boolean equals(OWLDomainClass first, OWLDomainClass second) {
        return equals(
                first.getOwlClass(),
                second.getOwlClass()
        );
    }

    /**
     * Являются ли два литерала эквивалентными
     * @param first - этот
     * @param second - и этот
     * @return
     */
    public static final boolean equals(OWLLiteral first, OWLLiteral second) {
        return StringUtils.equalsIgnoreCase(
                first.getLiteral(),
                second.getLiteral()
        );
    }

    /**
     * Являются ли два объекта эквивалентными
     * @param first - этот
     * @param second - и этот
     * @return
     */
    public static final boolean equals(OWLIndividual first, OWLIndividual second) {
        return StringUtils.equalsIgnoreCase(
                first.toStringID(),
                second.toStringID()
        );
    }

    /**
     * Являются ли два идентификатора эквивалентными
     * @param first - этот
     * @param second - и этот
     * @return
     */
    public static boolean equals(IRI first, IRI second) {
        return StringUtils.equalsIgnoreCase(
                first.toQuotedString(),
                second.toQuotedString()
        );
    }

    /**
     * Являются ли два свойства эквивалентными
     * @param first - это
     * @param second - и это
     * @return
     */
    public static final boolean equals(OWLProperty first, OWLProperty second) {
        return equals(first.getIRI(), second.getIRI());
    }
}
