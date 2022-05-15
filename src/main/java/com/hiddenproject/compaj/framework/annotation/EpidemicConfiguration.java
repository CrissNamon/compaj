package com.hiddenproject.compaj.framework.annotation;

import com.hiddenproject.compaj.framework.types.ModellingType;

public @interface EpidemicConfiguration {
  String name();
  Phase[] phases() default {};
  ModellingType type() default ModellingType.ITERABLE;
}
