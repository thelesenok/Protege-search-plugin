package ru.mydesignstudio.protege.plugin.search.service.swrl;

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
import ru.mydesignstudio.protege.plugin.search.api.service.SwrlService;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.SelectQueryToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.FromTypeSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.IndividualToSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartBuilder;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part.WherePartsCollectionSwrlConverter;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.AttributiveSearchStrategy;
import ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.AttributiveProcessorParams;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by abarmin on 27.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SwrlServiceImplTest {
    @Mock
    private OWLService owlService;
    private SwrlService swrlService;

    @Before
    public void setUp() throws Exception {
        swrlService = new SwrlServiceImpl(
                new SelectQueryToSwrlConverter(
                        new FromTypeSwrlConverter(),
                        new IndividualToSwrlConverter(owlService),
                        new WherePartsCollectionSwrlConverter(
                                new WherePartSwrlConverter()
                        )
                ),
                owlService
        );
        /**
         * Обучим моки правильному поведению
         */
        Mockito.doReturn(new OWLClassImpl(IRI.create("prefix", "Parent")))
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
                IRI.create("prefix", "Person")
        )));
        query.addWherePart(
                WherePartBuilder.builder()
                        .property("property1")
                        .equalTo("value1")
                        .build()
        );
        query.addWherePart(
                WherePartBuilder.builder()
                        .and()
                        .property("property2")
                        .moreThan(2)
                        .build()
        );
        //
        final IRI individualIri = IRI.create("Ivan");
        final Collection<LookupParam> lookupParams = Collections.singleton(
                new LookupParam(
                        new AttributiveSearchStrategy(),
                        new AttributiveProcessorParams(
                                query, false
                        )
                )
        );
        //
        final String swrl = swrlService.convertToSwrl(individualIri, lookupParams);
        //
        Assert.assertEquals("SWRL conversion fails", "Person(?object) ^ property1(?object, ?prop0) ^ swrlb:stringEqualIgnoreCase(?prop0, \"value1\") ^ property2(?object, ?prop1) ^ swrlb:greaterThan(?prop1, 2) -> Parent(Ivan)", swrl);
    }

}