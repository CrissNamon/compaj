package com.hiddenproject.compaj.core.data;

public interface Variable<N, D extends Number> {
  N getName();
  D g();
  void b(D data);
}
