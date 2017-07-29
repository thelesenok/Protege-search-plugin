package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by abarmin on 26.06.17.
 */
public class DataPropertyWeightFactoryTest {
    private DataPropertyWeightFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new DataPropertyWeightFactory();
    }

    @Test
    public void checkPriority() throws Exception {
        final InputStream streamResource = getClass().getClassLoader().getResourceAsStream(DataPropertyWeightFactoryTest.class.getSimpleName() + ".xml");
        final String xmlString = IOUtils.toString(streamResource);
        final double value = factory.build(xmlString);
        //
        Assert.assertEquals("Values is not equal", 0.2, value, 0.0);
    }
}