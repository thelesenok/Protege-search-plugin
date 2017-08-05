package ru.mydesignstudio.protege.plugin.search.test;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.domain.OWLDomainIndividual;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.StringUtils;

import java.util.Collection;

/**
 * Supplementary methods for simplifying testing
 */
public final class TestUtils {
    /**
     * Get ontology class by name
     * @param owlService - OWLService instance
     * @param className - name of class to find
     * @return - class instance
     * @throws ApplicationException - if class can not be found
     */
    public static final OWLClass getOwlClass(OWLService owlService, String className) throws ApplicationException {
        return CollectionUtils.findFirst(owlService.getClasses(), new Specification<OWLClass>() {
            @Override
            public boolean isSatisfied(OWLClass owlClass) {
                return StringUtils.equalsIgnoreCase(
                        owlClass.getIRI().getFragment(),
                        className
                );
            }
        });
    }

    /**
     * Get property by name
     * @param owlService - OWLService instance
     * @param owlClass - class for which searching properties
     * @param propertyName - property name
     * @return - property
     * @throws ApplicationException - if property can not be found
     */
    public static final OWLProperty getOwlProperty(OWLService owlService, OWLClass owlClass, String propertyName) throws ApplicationException {
        final OWLDataProperty dataProperty = CollectionUtils.findFirst(owlService.getDataProperties(owlClass), new Specification<OWLDataProperty>() {
            @Override
            public boolean isSatisfied(OWLDataProperty owlDataProperty) {
                return StringUtils.equalsIgnoreCase(
                        owlDataProperty.getIRI().getFragment(),
                        propertyName
                );
            }
        });
        if (dataProperty != null) {
            return dataProperty;
        }
        return CollectionUtils.findFirst(owlService.getObjectProperties(owlClass), new Specification<OWLObjectProperty>() {
            @Override
            public boolean isSatisfied(OWLObjectProperty owlObjectProperty) {
                return StringUtils.equalsIgnoreCase(
                        owlObjectProperty.getIRI().getFragment(),
                        propertyName
                );
            }
        });
    }

    /**
     * Get individual of selected class
     * @param owlService - OWLService instance
     * @param owlClass - instances of which class we want to select
     * @param individualName - individual name
     * @return - individual
     * @throws ApplicationException - if individual can not be found
     */
    public static final OWLDomainIndividual getOwlIndividual(OWLService owlService, OWLClass owlClass,
                                                             String individualName) throws ApplicationException {

        final Collection<OWLNamedIndividual> allIndividuals = owlService.getIndividuals(owlClass);
        final OWLNamedIndividual individual = CollectionUtils.findFirst(allIndividuals, new Specification<OWLNamedIndividual>() {
            @Override
            public boolean isSatisfied(OWLNamedIndividual owlNamedIndividual) {
                return StringUtils.equalsIgnoreCase(
                        owlNamedIndividual.getIRI().getFragment(),
                        individualName
                );
            }
        });
        return new OWLDomainIndividual(individual);
    }
}
