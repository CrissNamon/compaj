package com.hiddenproject.compaj.core.data;

import java.util.function.Supplier;

public interface Variable<N, D> extends Constant<N, D> {
  Supplier<D> getBinder();
  void setData(D data);
  void bind(Supplier<D> formula);
}
