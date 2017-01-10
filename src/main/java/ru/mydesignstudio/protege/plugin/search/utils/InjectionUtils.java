package ru.mydesignstudio.protege.plugin.search.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.mydesignstudio.protege.plugin.search.config.ModuleConfig;

/**
 * Created by abarmin on 03.01.17.
 */
public class InjectionUtils {
    public static final <T> T getInstance(Class<T> targetClass) {
        final Injector injector = Guice.createInjector(new ModuleConfig());
        return injector.getInstance(targetClass);
    }
}
