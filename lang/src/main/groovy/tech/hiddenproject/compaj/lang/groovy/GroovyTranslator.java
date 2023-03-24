package tech.hiddenproject.compaj.lang.groovy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.stream.Collectors;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import tech.hiddenproject.compaj.lang.CodeTranslation;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent.GLOBAL;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

/**
 * Implementation of {@link Translator} for Groovy.
 */
public class GroovyTranslator implements Translator {

  private static final String SCRIPT_BASE = "CompaJScriptBase";

  public static final Set<String> HIDDEN_VARIABLES = Set.of(
      "out",
      "$compajOut",
      "$compajOutputStream"
  );
  private static final List<CompilationCustomizer> customizerList = new ArrayList<>();
  private static final ImportCustomizer importCustomizer;
  private static final String[] normalImports =
      new String[]{
          "tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase",
          "org.apache.commons.math3.analysis.MultivariateFunction",
          "java.lang.reflect.Array",
          "java.lang.reflect.ParameterizedType",
          "java.lang.reflect.Type",
          "tech.hiddenproject.compaj.core.model.DynamicFunction",
          };

  private static final String[] starImports =
      new String[]{
          "tech.hiddenproject.compaj.core.data",
          "tech.hiddenproject.compaj.core.data.base",
          "tech.hiddenproject.compaj.core.model.base",
          "tech.hiddenproject.compaj.core.model",
          "tech.hiddenproject.compaj.applied.epidemic",
          "org.apache.commons.math3.ode.nonstiff",
          "org.apache.commons.math3.linear",
          "java.util.stream",
          "tech.hiddenproject.aide.optional"
      };

  private static final String[] staticStarsImports = new String[]{"java.lang.Math"};

  static {
    importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(normalImports);
    importCustomizer.addStarImports(starImports);
    importCustomizer.addStaticStars(staticStarsImports);
    customizerList.add(importCustomizer);
  }

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;
  private final Map<String, Object> variables;
  private final ByteArrayOutputStream output;

  private final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();

  public GroovyTranslator(TranslatorUtils translatorUtils) {
    this(SCRIPT_BASE, translatorUtils, new ArrayList<>());
  }

  public GroovyTranslator(TranslatorUtils translatorUtils, List<String> libraries) {
    this(SCRIPT_BASE, translatorUtils, libraries);
  }

  public GroovyTranslator(String base, TranslatorUtils translatorUtils, List<String> libraries) {
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    List<String> cp = compilerConfiguration.getClasspath();
    cp.add(libraries.get(0));
    compilerConfiguration.setClasspathList(cp);
    compilerConfiguration.addCompilationCustomizers(
        customizerList.toArray(new CompilationCustomizer[]{})
    );
    compilerConfiguration.setScriptBaseClass(base);
    importPluginClasses(getPluginImports(loadPluginsClasses()));
    this.binding = new Binding();
    this.output = new ByteArrayOutputStream();
    this.binding.setVariable("out", new PrintStream(output));
    this.variables = binding.getVariables();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
  }

  public static ImportCustomizer getImportCustomizer() {
    return importCustomizer;
  }

  public Object evaluate(String script) {
    return evaluate(script, Set.of());
  }

  public CompilerConfiguration getConfig() {
    return compilerConfiguration;
  }

  @Override
  public Object evaluate(String script, Set<CodeTranslation> codeTranslations) {
    output.reset();
    Script res = groovyShell.parse(translatorUtils.translate(script, codeTranslations));
    Object result = res.run();
    updateVariables();
    if (result != null) {
      getBinding().setVariable("trn", result);
    }
    if (output.toString().isEmpty()) {
      return result;
    }
    return output.toString();
  }

  @Override
  public TranslatorUtils getTranslatorUtils() {
    return translatorUtils;
  }

  @Override
  public Set<String> getHiddenVariables() {
    return HIDDEN_VARIABLES;
  }

  private void updateVariables() {
    Map<String, Object> tmp = getBinding().getVariables();
    for (Map.Entry<String, Object> entry : tmp.entrySet()) {
      variables.put(entry.getKey(), entry.getValue());
    }
  }

  private Binding getBinding() {
    return binding;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  private Class[] getLoadedClasses() {
    return groovyShell.getClassLoader().getLoadedClasses();
  }

  private GroovyShell getGroovyShell() {
    return groovyShell;
  }

  private void importPluginClasses(List<String> pluginClassesNames) {
    pluginClassesNames.forEach(importCustomizer::addImports);
  }

  private List<Class<?>> loadPluginsClasses() {
    return ServiceLoader.load(CompaJPlugin.class).stream()
        .map(Provider::get)
        .peek(compaJPlugin -> EventPublisher.INSTANCE.sendTo(GLOBAL.STARTUP, new CompaJEvent(GLOBAL.STARTUP, null)))
        .flatMap(plugin -> plugin.getClasses().stream())
        .collect(Collectors.toList());
  }

  private List<String> getPluginImports(List<Class<?>> classes) {
    return classes.stream().map(Class::getCanonicalName).collect(Collectors.toList());
  }
}
