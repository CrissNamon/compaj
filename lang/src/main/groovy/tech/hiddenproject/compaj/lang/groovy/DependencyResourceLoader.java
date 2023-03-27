package tech.hiddenproject.compaj.lang.groovy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import groovy.lang.GroovyResourceLoader;
import groovy.util.ResourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyResourceLoader implements GroovyResourceLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(DependencyResourceLoader.class);
  private final GroovyResourceLoader parentLoader;
  private final Set<String> scriptExtensions;

  private final ResourceConnector resourceConnector;

  public DependencyResourceLoader(GroovyResourceLoader parentLoader, Set<String> scriptExtensions,
                                  ResourceConnector resourceConnector) {
    this.parentLoader = parentLoader;
    this.scriptExtensions = scriptExtensions;
    this.resourceConnector = resourceConnector;
  }

  @Override
  public URL loadGroovySource(String s) throws MalformedURLException {
    for (String extension : scriptExtensions) {
      String filename = s.replace('.', File.separatorChar) + "." + extension;
      try {
        URLConnection dependentScriptConn = resourceConnector.getResourceConnection(filename);
        return dependentScriptConn.getURL();
      } catch (Throwable t) {
        LOGGER.debug("Dependency loading error: {}", filename);
      }
    }
    return parentLoader.loadGroovySource(s);
  }
}
