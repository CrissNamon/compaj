package com.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.Map;
import com.hiddenproject.compaj.core.data.DataGetter;
import com.hiddenproject.compaj.core.data.Equation;
import org.apache.commons.math3.linear.RealVector;

public interface Model<N> extends DataGetter<Double> {
  boolean a(Equation<N, Double> e, Double... data);
  boolean a(Equation<N, Double> e, RealVector data);
  List<Equation<N, Double>> a(List<N> label);
  List<Equation<N, Double>> a(List<N> label, Double... initializer);
  List<Equation<N, Double>> a(List<N> label, List<Double> initializer);
  void ad(N variable, Double... data);
  Map<N, Equation<N, Double>> eqs();
  Map<N, List<Double>> eqslog();
  boolean isCase(Equation<N, Double> eq);
  boolean isCase(N eq);

  @Override
  Double v(String label);

  @Override
  Double v(String label, int position);
  void compute();
  String getName();
  void bu(VoidSupplier binder);
}
