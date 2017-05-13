package ru.mydesignstudio.protege.plugin.search.utils;

import ru.mydesignstudio.protege.plugin.search.api.exception.ApplicationRuntimeException;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Created by abarmin on 13.05.17.
 *
 * Служебные рефлексивные методы
 */
public class ReflectionUtils {
    public static final void setValue(Object target, String fieldName, Object value) {
        final Collection<Field> fields = ClassUtils.getFields(target.getClass());
        final Field targetField = CollectionUtils.findFirst(fields, new Specification<Field>() {
            @Override
            public boolean isSatisfied(Field field) {
                return StringUtils.equalsIgnoreCase(
                        field.getName(),
                        fieldName
                );
            }
        });
        if (targetField == null) {
            throw new ApplicationRuntimeException(String.format(
                    "There is no field with name %s",
                    fieldName
            ));
        }
        try {
            targetField.setAccessible(true);
            targetField.set(target, value);
        } catch (Exception e) {
            throw new ApplicationRuntimeException(String.format(
                    "Can't set field %s value",
                    fieldName
            ));
        }
    }
}
