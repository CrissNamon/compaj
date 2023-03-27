package tech.hiddenproject.compaj.lang.groovy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
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
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.compaj.lang.CodeTranslation;
import tech.hiddenproject.compaj.lang.FileUtils;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent.GLOBAL;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

/**
 * Implementation of {@link Translator} for Groovy.
 */
public class GroovyTranslator implements Translator {

  private static final Logger LOGGER = LoggerFactory.getLogger(GroovyTranslator.class);
  private final List<CompilationCustomizer> customizerList = new ArrayList<>();
  private final ImportCustomizer importCustomizer;
  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;
  private final Map<String, Object> variables;
  /**
   * Output from Groovy console.
   */
  private final ByteArrayOutputStream output;
  private final CompilerConfiguration compilerConfiguration;
  /**
   * Libraries to add on classpath.
   */
  private final List<String> libraries = new ArrayList<>();
  /**
   * Temporal file to evaluate input from.
   */
  private final File tmpFile;
  private final PrintStream standardOut = System.out;

  public GroovyTranslator(TranslatorUtils translatorUtils, List<String> libraries) {
    this(translatorUtils, libraries, TranslatorProperties.PLUGINS_DIR, TranslatorProperties.DEFAULT_TMP_FILE);
  }

  public GroovyTranslator(TranslatorUtils translatorUtils, List<String> libraries,
                          String pluginsDir, String temporalFilePath) {
    this.importCustomizer = new ImportCustomizer();
    this.compilerConfiguration = new CompilerConfiguration();
    this.libraries.addAll(libraries);
    initImports();
    initConfiguration(libraries);
    this.binding = new Binding();
    this.output = new ByteArrayOutputStream();
    this.variables = binding.getVariables();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
    this.tmpFile = new File(temporalFilePath);
    GroovyClassLoader groovyClassLoader = groovyShell.getClassLoader();
    ResourceConnector resourceConnector = new PluginResourceConnector(groovyClassLoader, pluginsDir);
    GroovyResourceLoader resourceLoader = new DependencyResourceLoader(
        groovyClassLoader.getResourceLoader(),
        compilerConfiguration.getScriptExtensions(),
        resourceConnector
    );

    importPluginClasses(getPluginImports(loadCompiledPlugins()));
    groovyClassLoader.setResourceLoader(resourceLoader);
    groovyShell.parse("class plugin{}");

    System.setOut(new PrintStream(output));

    loadCompiledPlugins();
    loadRawPlugins(pluginsDir);
    importLoadedClasses();

    EventPublisher.INSTANCE.sendTo(GLOBAL.STARTUP, new CompaJEvent(GLOBAL.STARTUP, null));
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
    BooleanOptional.of(Objects.nonNull(result))
        .ifTrueThen(() -> getBinding().setVariable("trn", result));
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
    return TranslatorProperties.HIDDEN_VARIABLES;
  }

  private void initImports() {
    importCustomizer.addImports(Imports.normalImports.toArray(String[]::new));
    importCustomizer.addStarImports(Imports.starImports.toArray(String[]::new));
    importCustomizer.addStaticStars(Imports.staticStarsImports.toArray(String[]::new));
    customizerList.add(importCustomizer);
  }

  private void initConfiguration(List<String> libraries) {
    List<String> cp = compilerConfiguration.getClasspath();
    cp.addAll(libraries);
    LOGGER.info("Classpath: {}", cp);
    compilerConfiguration.setClasspathList(cp);
    compilerConfiguration.addCompilationCustomizers(
        customizerList.toArray(new CompilationCustomizer[]{})
    );
    compilerConfiguration.setScriptBaseClass(TranslatorProperties.SCRIPT_BASE);
    compilerConfiguration.setDefaultScriptExtension(TranslatorProperties.DEFAULT_SCRIPT_EXTENSION);
    compilerConfiguration.setScriptExtensions(TranslatorProperties.SCRIPT_EXTENSIONS);
  }

  @Override
  public Map<String, Object> getVariables() {
    return variables;
  }

  @Override
  public PrintStream getStandardOut() {
    return standardOut;
  }

  @SuppressWarnings("unchecked")
  private void updateVariables() {
    Map<String, Object> tmp = getBinding().getVariables();
    variables.putAll(tmp);
  }

  private Binding getBinding() {
    return binding;
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
        .forEach(mainFile -> ThrowableOptional.sneaky(() -> groovyShell.parse(mainFile).run()));
    importLoadedClasses();
  }

  private List<String> getPluginImports(List<Class<?>> classes) {
    return classes.stream().map(Class::getCanonicalName).collect(Collectors.toList());
  }

  private void importLoadedClasses() {
    Set<String> newImports = Arrays.stream(groovyShell.getClassLoader().getLoadedClasses())
        .map(Class::getCanonicalName)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    Imports.normalImports.addAll(newImports);
    if (Imports.normalImports.isEmpty()) {
      return;
    }
    importCustomizer.addImports(Imports.normalImports.toArray(String[]::new));
    Imports.normalImports.clear();
    compilerConfiguration.addCompilationCustomizers(importCustomizer);
  }
}
