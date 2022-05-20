package com.hiddenproject.compaj.translator.extension;

import groovy.lang.Script;

public interface Extension {
  void extend(Script thisBase);
  default String getName() {
    return this.getClass().getName();
  }
}
