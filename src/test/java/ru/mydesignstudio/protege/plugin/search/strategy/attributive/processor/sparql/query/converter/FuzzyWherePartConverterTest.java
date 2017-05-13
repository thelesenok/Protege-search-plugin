package ru.mydesignstudio.protege.plugin.search.strategy.attributive.processor.sparql.query.converter;


import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by abarmin on 11.05.17.
 */
public class FuzzyWherePartConverterTest {
    final FuzzyWherePartConverter converter = new FuzzyWherePartConverter();

    @Test
    public void testWithSingleToken() throws Exception {
        final Collection<String> values = converter.createAvailableValues("abcdef", 1);
        Assert.assertEquals(6, values.size());
    }

    @Test
    public void testWithTwoTokens() throws Exception {
        final Collection<String> values = converter.createAvailableValues("abcdef", 2);
        Assert.assertEquals(15, values.size());
    }

    @Test
    public void testWithThreeTokens() throws Exception {
        final Collection<String> values = converter.createAvailableValues("abcdef", 3);
        Assert.assertEquals(16, values.size());
    }
}