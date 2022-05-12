package com.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.Map;
import com.hiddenproject.compaj.core.data.Constant;
import com.hiddenproject.compaj.core.data.Variable;

public interface Model<N, D> {
  void addVariable(Variable<N, D> variable);
  void setVariable(N variable, D data, int position);
  void addConstant(Constant<N, D> constant);
  Map<N, Variable<N, D>> variables();
  Map<N, List<D>> variablesLog();
  Map<N, Constant<N, D>> constants();
  D variable(N label);
  D variable(N label, int position);
  D constant(N label);
  void update();
  String getName();
}
