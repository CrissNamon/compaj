package tech.hiddenproject.compaj.lang.groovy;

import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import groovy.lang.GroovyClassLoader;
import groovy.util.ResourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.compaj.lang.FileUtils;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;

public class PluginResourceConnector implements ResourceConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(PluginResourceConnector.class);
  private final GroovyClassLoader groovyClassLoader;
  private final String pluginsDir;

  public PluginResourceConnector(GroovyClassLoader groovyClassLoader, String pluginsDir) {
    this.groovyClassLoader = groovyClassLoader;
    this.pluginsDir = pluginsDir;
  }

  @Override
  public URLConnection getResourceConnection(String filePath) {
    String classFilePath = filePathToClass(filePath);
    try {
      return getResourceFrom(classFilePath, groovyClassLoader,
                             GroovyTranslator.class.getClassLoader(),
                             CompaJScriptBase.class.getClassLoader(),
                             ClassLoader.getPlatformClassLoader())
          .orElseGet(() -> FileUtils.getFileUrl(pluginsDir + "/" + filePath)).openConnection();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<URL> getResourceFrom(String path, ClassLoader... classLoaders) {
    Optional<URL> optionalURL = Arrays.stream(classLoaders)
        .map(classLoader -> classLoader.getResource(path))
        .filter(Objects::nonNull)
        .findFirst();
    optionalURL.ifPresent(url -> Imports.normalImports.add(classToCanonicalName(path)));
    return optionalURL;
  }

  private String filePathToClass(String path) {
    return path.replace(".", "/").replace("/cjp", "") + ".class";
  }

  private String classToCanonicalName(String className) {
    return className.replace(".class", "").replace("/", ".");
  }

}
