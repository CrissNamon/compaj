package annotation;

import framework.ModellingType;

public @interface Epidemic {
  String name();
  Phase[] phases() default {};
  ModellingType type() default ModellingType.SIMPLE;
}
