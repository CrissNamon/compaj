package com.hiddenproject.compaj.lang.extension;

import groovy.lang.*;

public interface Extension {
  void extend(Script thisBase);

  default String getName() {
    return this.getClass().getName();
  }
}
