package ru.mydesignstudio.protege.plugin.search.reasoner.sparql;

import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Created by abarmin on 07.01.17.
 */
public interface SparqlInferenceFactory {
    SparqlReasoner createReasoner(OWLOntologyManager manager);
}
