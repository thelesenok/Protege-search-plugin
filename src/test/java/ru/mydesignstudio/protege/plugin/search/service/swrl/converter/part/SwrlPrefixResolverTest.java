package ru.mydesignstudio.protege.plugin.search.service.swrl.converter.part;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

/**
 * Created by abarmin on 28.06.17.
 */
public class SwrlPrefixResolverTest {
    private SwrlPrefixResolver resolver;

    @Before
    public void setUp() throws Exception {
        resolver = new SwrlPrefixResolver();
    }

    @Test
    public void extractPrefix() throws Exception {
        final String prefix = resolver.extractPrefix("http://www.owl-ontologies.com/generations.owl#");
        //
        Assert.assertEquals("Prefix extraction exception", "generations", prefix);
    }

    @Test
    public void extractPrefixFromIRI() throws Exception {
        final String prefix = resolver.extractPrefix(
                IRI.create("http://www.owl-ontologies.com/generations.owl#", "Person")
        );
        //
        Assert.assertEquals("Prefix extraction exception", "generations", prefix);
    }
}