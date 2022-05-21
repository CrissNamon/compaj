package com.hiddenproject.compaj.core.model;

import com.hiddenproject.compaj.core.data.*;

import java.util.*;
import java.util.function.*;

public interface Model<N, F, I, O> {
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
