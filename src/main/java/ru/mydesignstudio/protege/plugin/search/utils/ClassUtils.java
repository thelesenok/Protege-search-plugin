package ru.mydesignstudio.protege.plugin.search.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by abarmin on 08.05.17.
 *
 * Утилиты по работе с классами через рефлекшн
 */
public class ClassUtils {
    /**
     * Получить все поля у класса, которые аннотированы указанной аннотацией
     * @param targetClass - у этого класса ищем поля
     * @param annotationClass - вот с этой вот аннотацией
     * @return - коллекция полей
     */
    public static final Collection<Field> getFields(Class targetClass, Class annotationClass) {
        final Collection<Field> fields = new HashSet<>();
        if (!Object.class.equals(targetClass.getSuperclass())) {
            fields.addAll(getFields(targetClass.getSuperclass(), annotationClass));
        }
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Все поля указанного класса с учетом иерархии
     * @param targetClass - у этого класса ищем поля
     * @return - коллекция полей
     */
    public static final Collection<Field> getFields(Class targetClass) {
        final Collection<Field> fields = new HashSet<>();
        if (!Object.class.equals(targetClass.getSuperclass())) {
            fields.addAll(getFields(targetClass.getSuperclass()));
        }
        for (Field field : targetClass.getDeclaredFields()) {
            fields.add(field);
        }
        return fields;
    }
}
