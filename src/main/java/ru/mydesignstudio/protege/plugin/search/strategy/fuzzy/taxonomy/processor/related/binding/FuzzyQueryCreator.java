package ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related.binding;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import ru.mydesignstudio.protege.plugin.search.strategy.fuzzy.taxonomy.processor.related.FuzzyTaxonomyRelatedQueryCreator;

/**
 * Binding for {@link FuzzyTaxonomyRelatedQueryCreator} creator
 * @author abarmin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
@BindingAnnotation
public @interface FuzzyQueryCreator {

}
