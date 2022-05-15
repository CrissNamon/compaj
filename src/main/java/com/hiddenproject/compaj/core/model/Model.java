package com.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import com.hiddenproject.compaj.core.data.Constant;
import com.hiddenproject.compaj.core.data.DataGetter;
import com.hiddenproject.compaj.core.data.Variable;

public interface Model<N, D> extends DataGetter {
  Variable<N, D> a(N label, D data);
  Variable<N, D> a(N label);
  List<Variable<N, D>> a(List<N> label, List<D> data);
  List<Variable<N, D>> a(List<N> label);
  void ad(N variable, D... data);
  void a(Constant<N, D> constant);
  @Deprecated
  void b(String variableLabel, Supplier<D> binder);
  @Deprecated
  void b(String variableLabel, D fixedBinder);
  Map<N, Variable<N, D>> variables();
  Map<N, List<D>> variablesLog();
  Map<N, Constant<N, D>> constants();

  @Override
  D v(String label);

  @Override
  D v(String label, int position);

  D c(N label);
  void compute();
  String getName();
  void bindUpdater(VoidSupplier binder);
}
