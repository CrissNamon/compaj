package tech.hiddenproject.compaj.lang.groovy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.stream.Collectors;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyResourceLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import groovy.util.ResourceConnector;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.compaj.lang.CodeTranslation;
import tech.hiddenproject.compaj.lang.FileUtils;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent.GLOBAL;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

/**
 * Implementation of {@link Translator} for Groovy.
 */
public class GroovyTranslator implements Translator {

  public static final String DEFAULT_DIR = System.getProperty("user.home") + "/CompaJ/";
  public static final String PLUGINS_DIR = DEFAULT_DIR + "plugins/";
  public static final String DEFAULT_TMP_FILE = DEFAULT_DIR + "tmp.cjn";
  public static final Set<String> HIDDEN_VARIABLES = Set.of(
      "out",
      "$compajOut",
      "$compajOutputStream"
  );
  private static final Logger LOGGER = LoggerFactory.getLogger(GroovyTranslator.class);
  private static final String SCRIPT_BASE = "CompaJScriptBase";
  private static final List<CompilationCustomizer> customizerList = new ArrayList<>();
  private static final ImportCustomizer importCustomizer;
  private static final Set<String> normalImports =
      new HashSet<>(
          Set.of("tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase",
          "org.apache.commons.math3.analysis.MultivariateFunction",
          "java.lang.reflect.Array",
          "java.lang.reflect.ParameterizedType",
          "java.lang.reflect.Type",
          "tech.hiddenproject.compaj.core.model.DynamicFunction"
          )
      );

  private static final String[] starImports =
      new String[]{
          "tech.hiddenproject.compaj.core.data",
          "tech.hiddenproject.compaj.core.data.base",
          "tech.hiddenproject.compaj.core.model.base",
          "tech.hiddenproject.compaj.core.model",
          "tech.hiddenproject.compaj.applied.epidemic",
          "org.apache.commons.math3.ode.nonstiff",
          "org.apache.commons.math3.linear",
          "java.util.stream"
      };

  private static final String[] staticStarsImports = new String[]{"java.lang.Math"};

  static {
    importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(normalImports.toArray(String[]::new));
    importCustomizer.addStarImports(starImports);
    importCustomizer.addStaticStars(staticStarsImports);
    customizerList.add(importCustomizer);
  }

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;
  private final Map<String, Object> variables;
  private final ByteArrayOutputStream output;
  private final CompilerConfiguration compilerConfiguration;
  private final List<String> libraries = new ArrayList<>();
  private final File tmpFile;

  public GroovyTranslator(TranslatorUtils translatorUtils, List<String> libraries) {
    this(translatorUtils, libraries, PLUGINS_DIR, DEFAULT_TMP_FILE);
  }

  public GroovyTranslator(TranslatorUtils translatorUtils, List<String> libraries,
                          String pluginsDir, String temporalFilePath) {
    this.compilerConfiguration = new CompilerConfiguration();
    this.libraries.addAll(libraries);
    initConfiguration(SCRIPT_BASE, libraries);
    this.binding = new Binding();
    this.output = new ByteArrayOutputStream();
    this.binding.setVariable("out", new PrintStream(output));
    this.variables = binding.getVariables();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
    GroovyClassLoader groovyClassLoader = groovyShell.getClassLoader();
    ResourceConnector resourceConnector = new PluginResourceConnector(groovyClassLoader, pluginsDir);
    GroovyResourceLoader resourceLoader = new DependencyResourceLoader(
        groovyClassLoader.getResourceLoader(),
        compilerConfiguration.getScriptExtensions(),
        resourceConnector
    );
    importPluginClasses(getPluginImports(loadCompiledPlugins()));
    groovyShell.getClassLoader().setResourceLoader(resourceLoader);
    groovyShell.parse("class plugin{}");
    loadCompiledPlugins();
    loadRawPlugins(pluginsDir);
    importLoadedClasses();
    EventPublisher.INSTANCE.sendTo(GLOBAL.STARTUP, new CompaJEvent(GLOBAL.STARTUP, null));
    this.tmpFile = new File(temporalFilePath);
  }

  public static ImportCustomizer getImportCustomizer() {
    return importCustomizer;
  }

  public Object evaluate(String script) {
    return evaluate(script, Set.of());
  }

  @Override
  public Object evaluate(String script, Set<CodeTranslation> codeTranslations) {
    output.reset();
    FileUtils.writeToFile(tmpFile, translatorUtils.translate(script, codeTranslations));
    Script res = ThrowableOptional.sneaky(() -> groovyShell.parse(tmpFile));
    Object result = res.run();
    importLoadedClasses();
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

  private void initConfiguration(String base, List<String> libraries) {
    List<String> cp = compilerConfiguration.getClasspath();
    cp.addAll(libraries);
    LOGGER.info("Classpath: {}", cp);
    compilerConfiguration.setClasspathList(cp);
    compilerConfiguration.addCompilationCustomizers(
        customizerList.toArray(new CompilationCustomizer[]{})
    );
    compilerConfiguration.setScriptBaseClass(base);
    compilerConfiguration.setDefaultScriptExtension("cjp");
    compilerConfiguration.setScriptExtensions(Set.of("cjp"));
  }

  private void updateVariables() {
    Map<String, Object> tmp = getBinding().getVariables();
    variables.putAll(tmp);
  }

  private Binding getBinding() {
    return binding;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  private void importPluginClasses(List<String> pluginClassesNames) {
    pluginClassesNames.forEach(importCustomizer::addImports);
  }

  private List<Class<?>> loadCompiledPlugins() {
    URL[] libUrls = libraries.stream()
        .map(FileUtils::getFileUrl)
        .toArray(URL[]::new);
    try (URLClassLoader urlClassLoader = new URLClassLoader(libUrls)) {
      return ServiceLoader.load(CompaJPlugin.class, urlClassLoader).stream()
          .map(Provider::get)
          .flatMap(plugin -> plugin.getClasses().stream())
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void loadRawPlugins(String pluginsDir) {
    File file = new File(pluginsDir);
    String[] pluginDirectories = Optional.ofNullable(file.list((dir, name) -> new File(dir, name).isDirectory()))
        .orElse(new String[]{});
    Arrays.stream(pluginDirectories).map(pluginDir -> new File(pluginsDir + "/" + pluginDir + "/Main.cjp"))
        .filter(File::exists)
        .forEach(mainFile -> ThrowableOptional.sneaky(() -> groovyShell.parse(mainFile)));
    importLoadedClasses();
  }

  private List<String> getPluginImports(List<Class<?>> classes) {
    return classes.stream().map(Class::getCanonicalName).collect(Collectors.toList());
  }

  private void importLoadedClasses() {
    Arrays.stream(groovyShell.getClassLoader().getLoadedClasses())
        .filter(c -> Objects.nonNull(c.getCanonicalName()))
        .filter(c -> !normalImports.contains(c.getCanonicalName()))
        .forEach(c -> {
          normalImports.add(c.getCanonicalName());
          importCustomizer.addImports(c.getCanonicalName());
        });
  }
}
