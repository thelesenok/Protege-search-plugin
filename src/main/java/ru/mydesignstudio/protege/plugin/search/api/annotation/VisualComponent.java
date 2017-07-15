package ru.mydesignstudio.protege.plugin.search.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class is a visual component. Used for classes where @Component annotation can not be set
 * @author abarmin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VisualComponent {

}
