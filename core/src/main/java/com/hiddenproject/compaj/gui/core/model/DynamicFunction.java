package com.hiddenproject.compaj.gui.core.model;

public interface DynamicFunction<T, R> {
  R apply(T... args);
}
