package com.hiddenproject.compaj.annotation;

import com.hiddenproject.compaj.framework.WatchType;

public @interface WatchModel {
  WatchType type();
  String name() default  "";
}
