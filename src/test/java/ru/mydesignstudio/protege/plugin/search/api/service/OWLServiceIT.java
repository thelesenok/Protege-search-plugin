package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.OWLOntology;
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;

import javax.inject.Inject;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceIT {
    @Inject
    private OWLService owlService;

    @Test
    public void testServiceInjected() throws Exception {
        assertNotNull("Service injection failed", owlService);
    }

    @Test
    public void testLoadOntologyFromFile() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        final OWLOntology ontology = owlService.loadOntology(ontologyFile);
        assertNotNull("Can't load ontology from file", ontology);
    }

    @Test
    public void testGetOntologyAfterLoad() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        final OWLOntology loadedOntology = owlService.loadOntology(ontologyFile);
        final OWLOntology defaultOntology = owlService.getOntology();
        assertNotNull("Can't get default ontology", defaultOntology);
    }
}