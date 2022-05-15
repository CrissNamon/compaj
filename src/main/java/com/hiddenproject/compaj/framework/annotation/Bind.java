package com.hiddenproject.compaj.framework.annotation;

public @interface Bind {
  String variable();
  double initial() default 0.0;
}
