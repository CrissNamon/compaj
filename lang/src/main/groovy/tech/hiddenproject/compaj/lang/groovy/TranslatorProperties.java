package tech.hiddenproject.compaj.lang.groovy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TranslatorProperties {

  public static final String DEFAULT_DIR = System.getProperty("user.home") + "/CompaJ/";
  public static final String PLUGINS_DIR = DEFAULT_DIR + "plugins/";
  public static final String DEFAULT_TMP_FILE = DEFAULT_DIR + "tmp.cjn";
  public static final Set<String> HIDDEN_VARIABLES = Set.of();
  public static final String SCRIPT_BASE = CompaJScriptBase.class.getCanonicalName();
  public static final String DEFAULT_SCRIPT_EXTENSION = "cjp";
  public static final Set<String> SCRIPT_EXTENSIONS = Set.of("cjp", "cjn");
  public static String MAIN_FILE_NAME = "Main";

  public static class Imports {

    public static final Set<String> normalImports =
        Collections.synchronizedSet(
            new HashSet<>(
                Set.of("tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase",
                       "org.apache.commons.math3.analysis.MultivariateFunction",
                       "java.lang.reflect.Array",
                       "java.lang.reflect.ParameterizedType",
                       "java.lang.reflect.Type",
                       "tech.hiddenproject.compaj.core.model.DynamicFunction"
                )
            )
        );

    public static final Set<String> starImports =
        Collections.synchronizedSet(
            new HashSet<>(
                Set.of(
                    "tech.hiddenproject.compaj.core.data",
                    "tech.hiddenproject.compaj.core.data.base",
                    "tech.hiddenproject.compaj.core.model.base",
                    "tech.hiddenproject.compaj.core.model",
                    "tech.hiddenproject.compaj.applied.epidemic",
                    "org.apache.commons.math3.ode.nonstiff",
                    "org.apache.commons.math3.linear",
                    "java.util.stream"
                )
            )
        );

    public static final Set<String> staticStarsImports =
        Collections.synchronizedSet(
            new HashSet<>(
                Set.of("java.lang.Math")
            )
        );
  }
}
