package com.hiddenproject.compaj.gui.core.model;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.hiddenproject.compaj.gui.core.data.NamedFunction;

public interface Model<N, F, I, O>  {
  O getAt(F label);
  boolean a(NamedFunction<F, I, O> e);
  void ad(N variable, Double... data);
  Map<N, NamedFunction<F, I, O>> fns();
  Map<N, List<O>> fnslog();
  boolean isCase(NamedFunction<F, I, O> eq);
  boolean isCase(N eq);
  O getAt(F label, int position);
  void compute(Map<F, I[]> data);
  void compute();
  String getName();
  void clear();
  void clear(F f);
  void bu(Consumer<Map<F, I[]>> binder);
}
