package com.hiddenproject.compaj.core.model;

import java.util.*;
import java.util.function.*;
import com.hiddenproject.compaj.core.data.*;

public interface Model<F, I, O> {
  O getAt(F label);

  void a(NamedFunction<F, I, O>... e);

  void ad(F variable, O... data);

  Map<F, NamedFunction<F, I, O>> fns();

  Map<F, List<O>> fnslog();

  O getAt(F label, int position);

  void compute(Map<F, I[]> data);

  void compute();

  String getName();

  void clear();

  void clear(F f);

  void bu(Consumer<Map<F, I[]>> binder);
}
