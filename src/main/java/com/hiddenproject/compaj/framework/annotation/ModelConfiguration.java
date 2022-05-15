package com.hiddenproject.compaj.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModelConfiguration {
  String name();
  ModelConstant[] constants() default {};
}
