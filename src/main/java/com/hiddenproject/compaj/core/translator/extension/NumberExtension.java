package com.hiddenproject.compaj.core.translator.extension;

import com.hiddenproject.compaj.core.data.Variable;

public class NumberExtension {

  public static Double plus(Number self, Variable arg) {
    return self.doubleValue() + arg.g().doubleValue();
  }

}
