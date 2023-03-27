package tech.hiddenproject.compaj.lang.groovy;

import java.util.HashSet;
import java.util.Set;

import groovy.lang.Script;
import tech.hiddenproject.aide.optional.ThrowableOptional;
import tech.hiddenproject.compaj.lang.exception.ExtensionException;
import tech.hiddenproject.compaj.lang.extension.Extension;

/**
 * Custom script base for {@link groovy.lang.GroovyShell}.
 */
public abstract class CompaJScriptBase extends Script {

  private static final Set<Extension> extensions;

  static {
    extensions = new HashSet<>();
  }

  public CompaJScriptBase() {
    extensions.forEach(e -> ThrowableOptional.sneaky(() -> e.extend(CompaJScriptBase.this),
                                                     ex -> new ExtensionException(e)));
  }

  public static void addExtension(Extension e) {
    extensions.add(e);
  }

  public void help() {
    println("Help is not available now...");
  }
}
