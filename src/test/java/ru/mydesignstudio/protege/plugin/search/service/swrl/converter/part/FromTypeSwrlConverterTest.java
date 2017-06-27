package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import ru.mydesignstudio.protege.plugin.search.api.query.FromType;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

/**
 * Created by abarmin on 26.06.17.
 */
public class FromTypeSwrlConverterTest {
    private FromTypeSwrlConverter fromTypeConverter;

    @Before
    public void setUp() throws Exception {
        fromTypeConverter = new FromTypeSwrlConverter();
    }

    @Test
    public void convert() throws Exception {
        final FromType fromType = new FromType(new OWLClassImpl(
                IRI.create("prefix", "Person")
        ));
        Assert.assertEquals("FromType converter failed", "Person(?object)", fromTypeConverter.convert(fromType));
    }

}