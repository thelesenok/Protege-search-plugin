package ru.mydesignstudio.protege.plugin.search.reasoner.sparql;

import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Created by abarmin on 07.01.17.
 */
public class BasicSparqlReasonerFactory implements SparqlInferenceFactory {
    @Override
    public SparqlReasoner createReasoner(OWLOntologyManager manager) {
        return new BasicSparqlReasoner(manager);
    }
}
