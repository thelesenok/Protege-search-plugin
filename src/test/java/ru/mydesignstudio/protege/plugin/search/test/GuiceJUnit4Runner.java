package ru.mydesignstudio.protege.plugin.search.test;

import com.google.inject.Guice;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import ru.mydesignstudio.protege.plugin.search.config.ModuleConfig;

/**
 * JUnit runner for Google Guice. Specified for this project
 */
public class GuiceJUnit4Runner extends BlockJUnit4ClassRunner {
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public GuiceJUnit4Runner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object testedClass = super.createTest();
        Guice.createInjector(new ModuleConfig()).injectMembers(testedClass);
        return testedClass;
    }
}
