package ru.mydesignstudio.protege.plugin.search.service.owl;

import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.FuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.api.service.fuzzy.function.TriangularFuzzyFunction;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.FuzzyOWLServiceImpl;
import ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.function.FuzzyFunctionFactory;
import ru.mydesignstudio.protege.plugin.search.utils.ReflectionUtils;

/**
 * Created by abarmin on 13.05.17.
 */
public class FuzzyOWLServiceImplTest {
    private FuzzyOWLServiceImpl service = new FuzzyOWLServiceImpl();
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
        Assert.assertEquals(fuzzyFunction.evaluate(-1), 0.0);
        Assert.assertEquals(fuzzyFunction.evaluate(0), 0.0);
        Assert.assertEquals(fuzzyFunction.evaluate(5), 1.0);
        Assert.assertEquals(fuzzyFunction.evaluate(10), 0.0);
        Assert.assertEquals(fuzzyFunction.evaluate(3), 0.6);
    }

}