package com.hiddenproject.compaj.annotation;

import com.hiddenproject.compaj.framework.ModellingType;

public @interface Epidemic {
  String name();
  Phase[] phases() default {};
  ModellingType type() default ModellingType.SIMPLE;
}
