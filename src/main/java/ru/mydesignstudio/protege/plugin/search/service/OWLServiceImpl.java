package ru.mydesignstudio.protege.plugin.search.service;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyRange;
import ru.mydesignstudio.protege.plugin.search.api.query.ResultSet;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.config.OntologyConfig;
import ru.mydesignstudio.protege.plugin.search.service.sparql.query.SparqlQueryConverter;
import ru.mydesignstudio.protege.plugin.search.service.sparql.reasoner.BasicSparqlReasonerFactory;
import ru.mydesignstudio.protege.plugin.search.service.sparql.reasoner.SparqlInferenceFactory;
import ru.mydesignstudio.protege.plugin.search.service.sparql.reasoner.SparqlReasoner;
import ru.mydesignstudio.protege.plugin.search.service.sparql.reasoner.SparqlReasonerException;
import ru.mydesignstudio.protege.plugin.search.service.sparql.reasoner.SparqlResultSet;
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
    private OWLOntology getOntology() {
        return OntologyConfig.getOntology();
    }

    @Override
    public Collection<OWLClass> getClasses() {
        return getOntology().getClassesInSignature();
    }

    @Override
    public Collection<OWLObjectProperty> getObjectProperties(OWLClass owlClass) {
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
    public Collection<OWLDataProperty> getDataProperties(OWLClass owlClass) {
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
    public Collection<OWLPropertyRange> getPropertyRanges(OWLProperty owlProperty) {
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
    public Collection<OWLNamedIndividual> getIndividuals(OWLClass owlClass) {
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
    public ResultSet search(SelectQuery selectQuery) {
        try {
            final SparqlInferenceFactory factory = new BasicSparqlReasonerFactory();
            final SparqlReasoner reasoner = factory.createReasoner(OntologyConfig.getModelManager().getOWLOntologyManager());
            reasoner.precalculate();
            //
            final String sparqlQuery = SparqlQueryConverter.convert(selectQuery);
            final SparqlResultSet sparqlResultSet = reasoner.executeQuery(sparqlQuery);
            return sparqlResultSet;
        } catch (SparqlReasonerException e) {
            throw new RuntimeException(e);
        }
    }
}
