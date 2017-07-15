package ru.mydesignstudio.protege.plugin.search.service.swrl.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.service.search.path.ShortestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.search.path.TestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.search.path.VertexPath;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.FromTypeSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.IndividualToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.SwrlPrefixResolver;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartBuilder;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartsCollectionSwrlConverter;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

/**
 * Created by abarmin on 27.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SelectQueryToSwrlConverterTest {
    private SelectQueryToSwrlConverter converter;
    @Mock
    private OWLService owlService;
    private PathBuilder pathBuilder;
    private ExceptionWrapperService wrapperService = new ExceptionWrapperService();
    private SwrlPrefixResolver prefixResolver;

    @Before
    public void setUp() throws Exception {
        prefixResolver = new SwrlPrefixResolver();
        pathBuilder = new ShortestPathBuilder(owlService, wrapperService);
        converter = new SelectQueryToSwrlConverter(
                new FromTypeSwrlConverter(prefixResolver),
                new IndividualToSwrlConverter(owlService, prefixResolver),
                new WherePartsCollectionSwrlConverter(
                        new WherePartSwrlConverter(prefixResolver, pathBuilder)
                )
        );
        /**
         * Обучим моки правильному поведению
         */
        Mockito.doReturn(new OWLClassImpl(IRI.create("http://www.owl-ontologies.com/generations.owl#", "Parent")))
                .when(owlService).getIndividualClass(Mockito.any(OWLIndividual.class));
    }

    @Test
    public void covert() throws Exception {
        final SelectQuery query = new SelectQuery();
        query.setFrom(new FromType(new OWLClassImpl(
                IRI.create("http://www.owl-ontologies.com/generations.owl#", "Person")
        )));
        query.addWherePart(
                WherePartBuilder.builder()
                        .property("property1", "Person")
                        .equalTo("value1")
                        .build()
        );
        query.addWherePart(
                WherePartBuilder.builder()
                        .and()
                        .property("property2", "Person")
                        .moreThan(2)
                        .build()
        );
        final OWLIndividual individual = new OWLNamedIndividualImpl(
                IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/family_example.owl#", "Ivan")
        );
        final String swrl = converter.covert(individual, query);
        //
        Assert.assertEquals("SWRL conversion fails", "generations:Person(?object) ^ my_custom_prefix:property1(?object, ?prop0) ^ swrlb:stringEqualIgnoreCase(?prop0, \"value1\") ^ my_custom_prefix:property2(?object, ?prop1) ^ swrlb:greaterThan(?prop1, 2) -> generations:Parent(family_example:Ivan)", swrl);
    }

}