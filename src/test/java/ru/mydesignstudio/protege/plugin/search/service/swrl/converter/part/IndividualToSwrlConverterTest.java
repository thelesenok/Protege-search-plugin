package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import ru.mydesignstudio.protege.plugin.search.api.service.OWLService;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

/**
 * Created by abarmin on 26.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class IndividualToSwrlConverterTest {
    @Mock
    private OWLService owlService;
    private IndividualToSwrlConverter individualConverter;

    @Before
    public void setUp() throws Exception {
        individualConverter = new IndividualToSwrlConverter(owlService);
        /**
         * Обучим моки правильному поведению
         */
        Mockito.doReturn(new OWLClassImpl(IRI.create("prefix", "Parent")))
                .when(owlService).getIndividualClass(Mockito.any(OWLIndividual.class));
    }

    @Test
    public void convert() throws Exception {
        final OWLNamedIndividualImpl individual = new OWLNamedIndividualImpl(
                IRI.create("Ivan")
        );
        final String swrl = individualConverter.convert(individual);
        //
        Assert.assertEquals("Individual converter fails", "Parent(Ivan)", swrl);
    }

}