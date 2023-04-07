import java.util.ServiceLoader;
import java.util.stream.Collectors;

import groovy.lang.Script;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.compaj.lang.exception.ExtensionException;
import tech.hiddenproject.compaj.lang.extension.Extension;

/**
 * Custom script base for {@link groovy.lang.GroovyShell}.
 */
public abstract class CompaJScriptBase extends Script {

  public CompaJScriptBase() {
    ServiceLoader<Extension> extensionLoader = ServiceLoader.load(Extension.class);
    extensionLoader.stream().map(ServiceLoader.Provider::get).collect(Collectors.toList())
      .forEach(e -> ThrowableOptional.sneaky(() -> e.extend(CompaJScriptBase.this), ex -> new ExtensionException(e)));
  }

  public void help() {
    println("Help is not available now...");
  }
}
