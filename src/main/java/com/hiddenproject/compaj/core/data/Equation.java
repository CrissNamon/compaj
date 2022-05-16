package com.hiddenproject.compaj.core.data;

import java.util.function.Supplier;

public interface Equation<N, D extends Number> extends Variable<N, D> {
  Supplier<Number> getBinder();
  void b(Supplier<Number> formula);
}
