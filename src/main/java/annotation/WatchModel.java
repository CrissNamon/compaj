package annotation;

import framework.WatchType;

public @interface WatchModel {
  WatchType type();
  String name() default  "";
}
