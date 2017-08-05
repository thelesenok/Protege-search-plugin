package ru.mydesignstudio.protege.plugin.search.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import ru.mydesignstudio.protege.plugin.search.test.GuiceJUnit4Runner;

import javax.inject.Inject;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

@RunWith(GuiceJUnit4Runner.class)
public class OWLServiceLoadClassesIT {
    @Inject
    private OWLService owlService;

    @Before
    public void setUp() throws Exception {
        final URL ontologyFileUrl = getClass().getClassLoader().getResource("it/Ontology1.owl");
        final File ontologyFile = new File(ontologyFileUrl.toURI());
        owlService.loadOntology(ontologyFile);
    }

    @Test
    public void testGetTopLevelClass() throws Exception {
        final OWLClass thingClass = owlService.getTopClass();
        assertNotNull("Can't load most top ontology class");
        assertEquals("Top class is not a Thing class", "Thing", thingClass.getIRI().getFragment());
    }
}