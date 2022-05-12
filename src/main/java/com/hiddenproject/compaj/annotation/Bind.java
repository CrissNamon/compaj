package com.hiddenproject.compaj.annotation;

public @interface Bind {
  String compartment();
  double initial() default 0.0;
}
