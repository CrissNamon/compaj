package tech.hiddenproject.compaj.lang.extension;

import groovy.lang.Script;

/**
 * Extends {@link Script} with custom logic.
 */
public interface Extension {

  /**
   * Will be called on {@link Script} initialization.
   *
   * @param thisBase {@link Script}
   */
  void extend(Script thisBase);

  /**
   * @return Name of this extension
   */
  default String getName() {
    return this.getClass().getName();
  }
}
