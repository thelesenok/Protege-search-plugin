package ru.mydesignstudio.protege.plugin.search.service.swrl;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;

import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import ru.mydesignstudio.protege.plugin.search.api.query.SelectQuery;
import ru.mydesignstudio.protege.plugin.search.api.search.params.LookupParam;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import ru.mydesignstudio.protege.plugin.search.api.service.PathBuilder;
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.service.exception.wrapper.ExceptionWrapperService;
import ru.mydesignstudio.protege.plugin.search.service.search.path.ShortestPathBuilder;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.SelectQueryToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.FromTypeSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.IndividualToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.SwrlPrefixResolver;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartBuilder;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartsCollectionSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.rule.engine.SwrlEngineManager;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.AttributiveSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.component.AttributiveSearchStrategyParamsComponent;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessor;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

/**
 * Created by abarmin on 27.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SwrlServiceImplTest {
    @Mock
    private OWLService owlService;
    private ExceptionWrapperService wrapperService = new ExceptionWrapperService();
    private SwrlService swrlService;
    private PathBuilder pathBuilder;
    private SwrlPrefixResolver prefixResolver;

    @Before
    public void setUp() throws Exception {
        prefixResolver = new SwrlPrefixResolver();
        pathBuilder = new ShortestPathBuilder(owlService, wrapperService);
        swrlService = new SwrlServiceImpl(
                new SelectQueryToSwrlConverter(
                        new FromTypeSwrlConverter(prefixResolver),
                        new IndividualToSwrlConverter(
                                owlService,
                                prefixResolver
                        ),
                        new WherePartsCollectionSwrlConverter(
                                new WherePartSwrlConverter(prefixResolver, pathBuilder)
                        )
                ),
                owlService,
                new SwrlEngineManager(owlService)
        );
        /**
         * Обучим моки правильному поведению
         */
        Mockito.doReturn(new OWLClassImpl(IRI.create("http://www.owl-ontologies.com/generations.owl#", "Parent")))
                .when(owlService).getIndividualClass(Mockito.any(OWLIndividual.class));
        Mockito.doAnswer(new Answer<OWLIndividual>() {
            @Override
            public OWLIndividual answer(InvocationOnMock invocationOnMock) throws Throwable {
                final IRI iri = invocationOnMock.getArgumentAt(0, IRI.class);
                return new OWLNamedIndividualImpl(iri);
            }
        }).when(owlService).getIndividual(Mockito.any(IRI.class));
    }

    @Test
    public void convertToSwrl() throws Exception {
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
        //
        final IRI individualIri = IRI.create("https://wiki.csc.calpoly.edu/OntologyTutorial/family_example.owl#", "Ivan");
        final Collection<LookupParam> lookupParams = Collections.singleton(
                new LookupParam(
                        new AttributiveSearchStrategy(
                        		new AttributiveSearchStrategyParamsComponent(owlService, wrapperService),
                        		new AttributiveProcessor()
                        	),
                        new AttributiveProcessorParams(
                                query, false
                        )
                )
        );
        //
        final String swrl = swrlService.convertToSwrl(individualIri, lookupParams);
        //
        Assert.assertEquals("SWRL conversion fails", "generations:Person(?object) ^ my_custom_prefix:property1(?object, ?prop0) ^ swrlb:stringEqualIgnoreCase(?prop0, \"value1\") ^ my_custom_prefix:property2(?object, ?prop1) ^ swrlb:greaterThan(?prop1, 2) -> generations:Parent(family_example:Ivan)", swrl);
    }

}