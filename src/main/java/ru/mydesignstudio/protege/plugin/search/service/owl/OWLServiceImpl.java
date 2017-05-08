package ru.mydesignstudio.protege.plugin.search.service.owl;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.EmptyResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.SearchStrategy;
import ru.mydesignstudio.protege.plugin.search.api.search.collector.SearchProcessor;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.utils.CollectionUtils;
import ru.mydesignstudio.protege.plugin.search.utils.Specification;
import ru.mydesignstudio.protege.plugin.search.utils.Transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by abarmin on 03.01.17.
 */
public class OWLServiceImpl implements OWLService {
    private OWLOntology getOntology() throws ApplicationException {
        return OntologyConfig.getOntology();
    }

    @Override
    public Collection<OWLClass> getClasses() throws ApplicationException {
        return getOntology().getClassesInSignature();
    }

    @Override
    public Collection<OWLObjectProperty> getObjectProperties(OWLClass owlClass) throws ApplicationException {
        final Collection<OWLObjectProperty> properties = new ArrayList<>();
        final Set<OWLObjectPropertyDomainAxiom> axioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN);
        for (OWLObjectPropertyDomainAxiom axiom : axioms) {
            if (axiom.getDomain().equals(owlClass)) {
                properties.addAll(axiom.getObjectPropertiesInSignature());
            }
        }
        return properties;
    }

    @Override
    public Collection<OWLDataProperty> getDataProperties(OWLClass owlClass) throws ApplicationException {
        final Collection<OWLDataProperty> properties = new ArrayList<>();
        final Set<OWLDataPropertyDomainAxiom> axioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_DOMAIN);
        for (OWLDataPropertyDomainAxiom axiom : axioms) {
            if (axiom.getDomain().equals(owlClass)) {
                properties.addAll(axiom.getDataPropertiesInSignature());
            }
        }
        return properties;
    }

    @Override
    public Collection<OWLPropertyRange> getPropertyRanges(OWLProperty owlProperty) throws ApplicationException {
        final Collection<OWLPropertyRange> ranges = new ArrayList<>();
        final Set<OWLDataPropertyRangeAxiom> dataRangeAxioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_RANGE);
        for (OWLDataPropertyRangeAxiom dataRangeAxiom : dataRangeAxioms) {
            if (dataRangeAxiom.getSignature().contains(owlProperty)) {
                ranges.add(dataRangeAxiom.getRange());
            }
        }
        final Set<OWLObjectPropertyRangeAxiom> objectRangeAxioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_RANGE);
        for (OWLObjectPropertyRangeAxiom objectRangeAxiom : objectRangeAxioms) {
            if (objectRangeAxiom.getSignature().contains(owlProperty)) {
                ranges.add(objectRangeAxiom.getRange());
            }
        }
        return ranges;
    }

    @Override
    public Collection<OWLNamedIndividual> getIndividuals(OWLClass owlClass) throws ApplicationException {
        final Set<OWLClassAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.CLASS_ASSERTION);
        return CollectionUtils.map(CollectionUtils.filter(axioms, new Specification<OWLClassAssertionAxiom>() {
            @Override
            public boolean isSatisfied(OWLClassAssertionAxiom axiom) {
                return axiom.getClassesInSignature().contains(owlClass);
            }
        }), new Transformer<OWLClassAssertionAxiom, OWLNamedIndividual>() {
            @Override
            public OWLNamedIndividual transform(OWLClassAssertionAxiom item) {
                return (OWLNamedIndividual) item.getIndividual();
            }
        });
    }

    @Override
    public ResultSet search(Collection<LookupParam> params) throws ApplicationException {
        /**
         * Получим запрос по которому будем искать
         */
        SelectQuery selectQuery = null;
        for (LookupParam param : params) {
            final SearchStrategy strategy = param.getStrategy();
            final SearchProcessor processor = strategy.getSearchProcessor();
            selectQuery = processor.prepareQuery(selectQuery, param.getStrategyParams());
        }
        /**
         * Реализуем последовательный поиск разными стратегиями
         * на основе параметров, выбранных в компоненте
         */
        ResultSet resultSet = new EmptyResultSet();
        for (LookupParam param : params) {
            final SearchStrategy strategy = param.getStrategy();
            final SearchProcessor processor = strategy.getSearchProcessor();
            resultSet = processor.collect(resultSet, selectQuery, param.getStrategyParams());
        }
        return resultSet;
    }

    @Override
    public OWLClass getParentClass(OWLClass child) throws ApplicationException {
        final Set<OWLSubClassOfAxiom> axioms = getOntology().getAxioms(AxiomType.SUBCLASS_OF);
        for (OWLSubClassOfAxiom axiom : axioms) {
            for (OWLClass childClass : axiom.getSubClass().getClassesInSignature()) {
                if (child.equals(childClass)) {
                    final Set<OWLClass> parentClasses = axiom.getSuperClass().getClassesInSignature();
                    return parentClasses.iterator().next();
                }
            }
        }
        return null;
    }

    @Override
    public Collection<OWLClass> getChildrenClasses(OWLClass parent) throws ApplicationException {
        final Set<OWLSubClassOfAxiom> axioms = getOntology().getAxioms(AxiomType.SUBCLASS_OF);
        for (OWLSubClassOfAxiom axiom : axioms) {
            for (OWLClass owlClass : axiom.getSuperClass().getClassesInSignature()) {
                if (parent.equals(owlClass)) {
                    return axiom.getSubClass().getClassesInSignature();
                }
            }
        }
        return null;
    }

    @Override
    public OWLIndividual getIndividual(IRI uri) throws ApplicationException {
        final Set<OWLNamedIndividual> owlNamedIndividuals = getOntology().getIndividualsInSignature();
        return CollectionUtils.findFirst(owlNamedIndividuals, new Specification<OWLNamedIndividual>() {
            @Override
            public boolean isSatisfied(OWLNamedIndividual individual) {
                return individual.getIRI().equals(uri);
            }
        });
    }

    @Override
    public Object getPropertyValue(OWLIndividual individual, OWLProperty property) throws ApplicationException {
        // @TODO поправить эту копипасту. чет у меня не получилось прикастовать ровно
        if (property instanceof OWLObjectProperty) {
            final Set<OWLObjectPropertyAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
            final Collection<OWLObjectPropertyAssertionAxiom> subjectAxioms = CollectionUtils.filter(axioms, new Specification<OWLObjectPropertyAssertionAxiom>() {
                @Override
                public boolean isSatisfied(OWLObjectPropertyAssertionAxiom axiom) {
                    return axiom.getSubject().equals(individual) && axiom.getProperty().equals(property);
                }
            });
            if (CollectionUtils.isNotEmpty(subjectAxioms)) {
                return subjectAxioms.iterator().next().getObject();
            }
        } else if (property instanceof OWLDataProperty) {
            final Set<OWLDataPropertyAssertionAxiom> axioms = getOntology().getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
            final Collection<OWLDataPropertyAssertionAxiom> subjectAxioms = CollectionUtils.filter(axioms, new Specification<OWLDataPropertyAssertionAxiom>() {
                @Override
                public boolean isSatisfied(OWLDataPropertyAssertionAxiom axiom) {
                    return axiom.getSubject().equals(individual) && axiom.getProperty().equals(property);
                }
            });
            if (CollectionUtils.isNotEmpty(subjectAxioms)) {
                return subjectAxioms.iterator().next().getObject();
            }
        }
        return null;
    }
}
