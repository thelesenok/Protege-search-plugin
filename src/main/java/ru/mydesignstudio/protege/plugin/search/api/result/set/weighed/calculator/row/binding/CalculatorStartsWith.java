package ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.binding;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import ru.mydesignstudio.protege.plugin.search.api.result.set.weighed.calculator.row.ProximityCalculatorStartsWith;

/**
 * Binding for {@link ProximityCalculatorStartsWith}
 * @author abarmin
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
@BindingAnnotation
public @interface CalculatorStartsWith {

}
