package ru.mydesignstudio.protege.plugin.search.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;
import ru.mydesignstudio.protege.plugin.search.config.ModuleConfig;

import javax.inject.Inject;
import java.lang.reflect.Field;

/**
 * Created by abarmin on 03.01.17.
 *
 * Утилиты для работ по внедрению зависимостей
 */
public class InjectionUtils {
    /**
     * Получить бин указанного класса
     * @param targetClass - класс бина
     * @param <T> - бин
     * @return
     * @deprecated - так как нарушает DI-подход
     */
    @Deprecated
    public static final <T> T getInstance(Class<T> targetClass) {
        final Injector injector = Guice.createInjector(new ModuleConfig());
        return injector.getInstance(targetClass);
    }

    /**
     * Внедрить зависимости в указанный класс
     * @param target
     * @deprecated - так как нарушает DI-подход
     */
    @Deprecated
    public static final void injectInstances(Object target) {
        for (Field field : ClassUtils.getFields(target.getClass(), Inject.class)) {
            final Class<?> serviceClass = field.getType();
            final Object beanInstance = getInstance(serviceClass);
            if (beanInstance != null) {
                try {
                    field.setAccessible(true);
                    field.set(target, beanInstance);
                } catch (Exception e) {
                    throw new ApplicationRuntimeException(String.format(
                            "Can't set field %s on object %s",
                            field,
                            target
                    ));
                }
            }
        }
    }
}
