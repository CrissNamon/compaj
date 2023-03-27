package tech.hiddenproject.compaj.lang.exception;

import tech.hiddenproject.compaj.lang.extension.Extension;

public class ExtensionException extends RuntimeException {

  private static final String MESSAGE = "Unable to load extension: %s";

  public ExtensionException(Extension extension) {
    super(String.format(MESSAGE, extension.getName()));
  }
}
