package com.hiddenproject.compaj.core.data;

import java.util.function.Supplier;

public interface Variable<N, D> extends Constant<N, D> {
  Supplier<D> getBinder();
  void s(D data);
  void b(Supplier<D> formula);
  D g();
}
