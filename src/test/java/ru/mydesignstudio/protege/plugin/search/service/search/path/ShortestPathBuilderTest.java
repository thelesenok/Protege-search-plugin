package ru.mydesignstudio.protege.plugin.search.service.search.path;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLProperty;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationException;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectField;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PropertyBasedPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.util.Collection;

/**
 * Created by Aleksandr_Barmin on 6/30/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShortestPathBuilderTest {
    private PropertyBasedPathBuilder pathBuilder;
    @Mock
    private OWLService owlService;
    private ExceptionWrapperService wrapperService = new ExceptionWrapperService();

    @Before
    public void setUp() throws Exception {
        pathBuilder = new ShortestPathBuilder(
                owlService,
                wrapperService
        );
        /**
         * Обучим успешному маршруту
         */
        TestPathBuilder.builder(owlService)
                .addVertex(VertexPath.builder()
                        .from("A")
                        .through("Pab")
                        .to("B")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("A")
                        .through("Pac")
                        .to("C")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("C")
                        .through("Pcd")
                        .to("D")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("B")
                        .to("E")
                        .through("Pbe")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("E")
                        .through("Pea")
                        .to("A")
                        .build());
    }

    @Test
    public void testSourceAndDestinationAreTheSame() throws Exception {
        Collection<SelectField> path = pathBuilder.buildPath(buildClass("A"), buildClass("A"));
        //
        Assert.assertTrue("Same source and destination check fails", path.isEmpty());
    }

    @Test
    public void testPathInOneHop() throws Exception {
        Collection<SelectField> path = pathBuilder.buildPath(buildClass("A"), buildClass("B"));
        //
        Assert.assertTrue("One hop check fails", path.size() == 1);
        //
        final SelectField relation = path.iterator().next();
        Assert.assertTrue("Relation class check fails", relation.equals(new SelectField(
                buildClass("A"),
                buildProperty("Pab")
        )));
    }

    @Test
    public void testPathInTwoHops() throws Exception {
        final Collection<SelectField> path = pathBuilder.buildPath(buildClass("A"), buildClass("D"));
        //
        Assert.assertTrue("Two hops check fails", path.size() == 2);
    }

    @Test(expected = ApplicationException.class)
    public void testUnavailablePath() throws Exception {
        pathBuilder.buildPath(buildClass("C"), buildClass("B"));
    }

    private OWLProperty buildProperty(String propertyName) {
        return new OWLObjectPropertyImpl(
                IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/my_custom_prefix.owl#", propertyName)
        );
    }

    private OWLClass buildClass(String className) {
        return new OWLClassImpl(
                IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/my_custom_prefix.owl#", className)
        );
    }
}