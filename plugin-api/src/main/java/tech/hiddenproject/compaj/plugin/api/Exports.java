package tech.hiddenproject.compaj.plugin.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines classes that should be imported on startup.
 * CompaJ.readFile("/Users/danilarassohin/CompaJ/plugins/plugin.cjp")
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Exports {

  Class<?>[] value();

}
