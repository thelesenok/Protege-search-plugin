package ru.mydesignstudio.protege.plugin.search.service.owl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.TriangularFuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.FuzzyOWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.function.FuzzyFunctionFactory;
import ru.mydesignstudio.protege.plugin.search.utils.ReflectionUtils;

/**
 * Created by abarmin on 13.05.17.
 */
public class FuzzyOWLServiceImplTest {
    private FuzzyOWLServiceImpl service = new FuzzyOWLServiceImpl(null, null, null, null, null);
    private FuzzyFunctionFactory functionFactory = new FuzzyFunctionFactory();

    @Before
    public void setUp() throws Exception {
        ReflectionUtils.setValue(service, "functionFactory", functionFactory);
    }

    @Test
    public void parseFunction() throws Exception {
        final String triangleFunction = "<fuzzyOwl2 fuzzyType=\"datatype\">\n" +
                "<Datatype type=\"triangular\" a=\"0\" b=\"5\" c=\"10\" />\n" +
                "</fuzzyOwl2>";
        final FuzzyFunction fuzzyFunction = service.parseFunction(triangleFunction);
        Assert.assertEquals(TriangularFuzzyFunction.class, fuzzyFunction.getClass());
        //
        Assert.assertEquals(0.0, fuzzyFunction.evaluate(-1), 0.1);
        Assert.assertEquals(0.0, fuzzyFunction.evaluate(0), 0.1);
        Assert.assertEquals(1.0, fuzzyFunction.evaluate(5), 0.1);
        Assert.assertEquals(0.0, fuzzyFunction.evaluate(10), 0.1);
        Assert.assertEquals(0.6, fuzzyFunction.evaluate(3), 0.1);
    }

}