package ru.mydesignstudio.protege.plugin.search.config;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Created by abarmin on 04.01.17.
 */
public class OntologyConfig {
    private static OWLOntology ontology;
    private static OWLModelManager modelManager;

    public static OWLOntology getOntology() {
        if (ontology == null) {
            ontology = getModelManager().getActiveOntology();
        }
        return ontology;
    }

    public static void setOntology(OWLOntology ontology) {
        OntologyConfig.ontology = ontology;
    }

    public static OWLModelManager getModelManager() {
        if (modelManager == null) {
            throw new RuntimeException("Model manager is not set");
        }
        return modelManager;
    }

    public static void setModelManager(OWLModelManager modelManager) {
        OntologyConfig.modelManager = modelManager;
    }
}
