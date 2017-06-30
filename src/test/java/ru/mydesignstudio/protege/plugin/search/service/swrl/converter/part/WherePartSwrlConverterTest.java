package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import ru.mydesignstudio.protege.plugin.search.api.common.Pair;
import ru.mydesignstudio.protege.plugin.search.api.query.WherePart;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.service.search.path.ShortestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.search.path.TestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.search.path.VertexPath;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

/**
 * Created by abarmin on 26.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class WherePartSwrlConverterTest {
    private WherePartSwrlConverter wherePartConverter;
    @Mock
    private OWLService owlService;
    private ExceptionWrapperService wrapperService = new ExceptionWrapperService();
    private PathBuilder pathBuilder;

    @Before
    public void setUp() throws Exception {
        pathBuilder = new ShortestPathBuilder(owlService, wrapperService);
        wherePartConverter = new WherePartSwrlConverter(new SwrlPrefixResolver(), pathBuilder);
        /**
         * Обучим OWLService связям в графе классов
         */
        TestPathBuilder.builder(owlService)
                .addVertex(VertexPath.builder()
                        .from("A")
                        .to("B")
                        .through("Pab")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("B")
                        .to("E")
                        .through("Pbe")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("E")
                        .to("A")
                        .through("Pea")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("A")
                        .to("C")
                        .through("Pac")
                        .build())
                .addVertex(VertexPath.builder()
                        .from("C")
                        .to("D")
                        .through("Pcd")
                        .build());
    }

    private Pair<OWLClass, WherePart> createSameClassPair(WherePart wherePart) {
        final OWLClass owlClass = createClass("dummyClass");
        wherePart.setOwlClass(owlClass);
        return new Pair<>(owlClass, wherePart);
    }

    private OWLClass createClass(String className) {
        return new OWLClassImpl(
                IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/my_custom_prefix.owl#", className)
        );
    }

    @Test
    public void testWithoutConcatOperation() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .property("property0")
                .equalTo("value0")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 0);
        //
        Assert.assertEquals("Concat operation check fails", "my_custom_prefix:property0(?object, ?prop0) ^ swrlb:stringEqualIgnoreCase(?prop0, \"value0\")", swrl);
    }

    @Test
    public void testEqual() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property1")
                .equalTo("value1")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 1);
        //
        Assert.assertEquals("Equal converter fails", "^ my_custom_prefix:property1(?object, ?prop1) ^ swrlb:stringEqualIgnoreCase(?prop1, \"value1\")", swrl);
    }

    @Test
    public void testLike() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property2")
                .like("value2")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 2);
        //
        Assert.assertEquals("Like converter fails", "^ my_custom_prefix:property2(?object, ?prop2) ^ swrlb:matches(?prop2, \"value2\")", swrl);
    }

    @Test
    public void testContains() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property3")
                .contains("value3")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 3);
        //
        Assert.assertEquals("Contains converter fails", "^ my_custom_prefix:property3(?object, ?prop3) ^ swrlb:contains(?prop3, \"value3\")", swrl);
    }

    @Test
    public void testNotEquals() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property4")
                .notEqualsTo("value4")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 4);
        //
        Assert.assertEquals("Not equals converter fails", "^ my_custom_prefix:property4(?object, ?prop4) ^ swrlb:notEqual(?prop4, \"value4\")", swrl);
    }

    @Test
    public void testStartsWith() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property5")
                .startsWith("value5")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 5);
        //
        Assert.assertEquals("Starts with converter fails", "^ my_custom_prefix:property5(?object, ?prop5) ^ swrlb:startsWith(?prop5, \"value5\")", swrl);
    }

    @Test
    public void testEndsWith() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property6")
                .endsWith("value6")
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 6);
        //
        Assert.assertEquals("Ends with converter fails", "^ my_custom_prefix:property6(?object, ?prop6) ^ swrlb:endsWith(?prop6, \"value6\")", swrl);
    }

    @Test
    public void testMoreThan() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property7")
                .moreThan(7)
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 7);
        //
        Assert.assertEquals("More than converter fails", "^ my_custom_prefix:property7(?object, ?prop7) ^ swrlb:greaterThan(?prop7, 7)", swrl);
    }

    @Test
    public void testMoreOrEqualsThan() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property8")
                .moreOrEqualsTo(8)
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 8);
        //
        Assert.assertEquals("More or equals converter fails", "^ my_custom_prefix:property8(?object, ?prop8) ^ swrlb:greaterThanOrEqual(?prop8, 8)", swrl);
    }

    @Test
    public void testLessThan() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property9")
                .lessThan(9)
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 9);
        //
        Assert.assertEquals("Less than converter fails", "^ my_custom_prefix:property9(?object, ?prop9) ^ swrlb:lessThan(?prop9, 9)", swrl);
    }

    @Test
    public void testLessOrEqualsThan() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property10")
                .lessOrEqualsTo(10)
                .build();
        final String swrl = wherePartConverter.convert(createSameClassPair(wherePart), 10);
        //
        Assert.assertEquals("Less or equals converter fails", "^ my_custom_prefix:property10(?object, ?prop10) ^ swrlb:lessThanOrEqual(?prop10, 10)", swrl);
    }

    @Test
    public void testEqualsWithRelations() throws Exception {
        final WherePart wherePart = WherePartBuilder.builder()
                .and()
                .property("property11", "D")
                .equalTo("value11")
                .build();
        final String swrl = wherePartConverter.convert(new Pair<>(
                createClass("A"),
                wherePart
        ), 11);
        //
        Assert.assertEquals("Relations converter fails", "^ my_custom_prefix:Pac(?object, ?relation_0_11) ^ my_custom_prefix:Pcd(?relation_0_11, ?relation_1_11) ^ my_custom_prefix:property11(?relation_1_11, ?prop11) ^ swrlb:stringEqualIgnoreCase(?prop11, \"value11\")", swrl);
    }
}