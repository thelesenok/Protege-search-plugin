package ru.mydesignstudio.protege.plugin.search.config;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;

/**
 * Created by abarmin on 04.01.17.
 */
public class OntologyConfig {
    private static OWLOntology ontology;
    private static OWLModelManager modelManager;
    private static OWLOntologyManager ontologyManager;

    public static OWLOntology getOntology() throws ApplicationException {
        if (ontology == null) {
            ontology = getModelManager().getActiveOntology();
        }
        return ontology;
    }

    public static void setOntology(OWLOntology ontology) {
        OntologyConfig.ontology = ontology;
    }

    public static OWLModelManager getModelManager() throws ApplicationException {
        if (modelManager == null) {
            throw new ApplicationException("Model manager is not set");
        }
        return modelManager;
    }

    public static OWLOntologyManager getOntologyManager() throws ApplicationException {
        if (OntologyConfig.ontologyManager == null) {
            OntologyConfig.ontologyManager = getModelManager().getOWLOntologyManager();
        }
        return ontologyManager;
    }

    public static void setOntologyManager(OWLOntologyManager ontologyManager) {
        OntologyConfig.ontologyManager = ontologyManager;
    }

    public static void setModelManager(OWLModelManager modelManager) {
        OntologyConfig.modelManager = modelManager;
    }
}
