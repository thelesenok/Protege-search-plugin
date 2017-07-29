package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by abarmin on 29/07/2017.
 */
public class ObjectPropertyWeightFactoryTest {
    private PropertyWeightFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new ObjectPropertyWeightFactory();
    }

    @Test
    public void checkObjectPropertyWeight() throws Exception {
        final InputStream streamResource = getClass().getClassLoader().getResourceAsStream(ObjectPropertyWeightFactoryTest.class.getSimpleName() + ".xml");
        final String xmlString = IOUtils.toString(streamResource);
        final double value = factory.build(xmlString);
        //
        Assert.assertEquals("Object property weight is not set", 0.5, value, 0.0);
    }

}