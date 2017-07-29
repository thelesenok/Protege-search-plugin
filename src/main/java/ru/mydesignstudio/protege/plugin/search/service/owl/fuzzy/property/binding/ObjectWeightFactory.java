package ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.binding;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by abarmin on 29/07/2017.
 *
 * Binding for {@link ru.mydesignstudio.protege.plugin.search.service.owl.fuzzy.property.ObjectPropertyWeightFactory}
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
public @interface ObjectWeightFactory {
}
