package com.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.hiddenproject.compaj.core.data.NamedFunction;
import org.apache.commons.math3.linear.RealVector;

public interface Model<N, F, I, O>  {
  void putAt(F label, NamedFunction<F, I, O> e);
  O getAt(F label);
  boolean a(NamedFunction<F, I, O> e, O... data);
  boolean a(NamedFunction<F, I, O> e, RealVector data);
  void ad(N variable, Double... data);
  Map<N, NamedFunction<F, I, O>> fns();
  Map<N, List<O>> fnslog();
  boolean isCase(NamedFunction<F, I, O> eq);
  boolean isCase(N eq);
  O getAt(F label, int position);
  void call(Map<F, I[]> data);
  void call();
  String getName();
  void clear();
  void clear(F f);
  void bu(Consumer<Map<F, Double[]>> binder);
}
