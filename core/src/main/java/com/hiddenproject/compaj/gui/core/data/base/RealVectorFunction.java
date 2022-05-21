package com.hiddenproject.compaj.gui.core.data.base;

import java.util.List;
import com.hiddenproject.compaj.gui.core.data.NamedFunction;
import com.hiddenproject.compaj.gui.core.model.DynamicFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

public class RealVectorFunction implements NamedFunction<String, Double, Double[]>, MultivariateVectorFunction {

  protected final String name;
  protected DynamicFunction<Double, Double[]> formula;

  public RealVectorFunction(String name, Double[] initial) {
    this(name, (x) -> initial);
  }

  public RealVectorFunction(String name) {
    this(name, (d) -> new Double[0]);
  }

  public RealVectorFunction(String name, DynamicFunction<Double, Double[]> f) {
    this.name = name;
    this.formula = f;
  }

  @Override
  public DynamicFunction<Double, Double[]> getBinder() {
    return formula;
  }

  @Override
  public void b(DynamicFunction<Double, Double[]> formula) {
    this.formula = formula;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Double[] value(Double... data) {
    return formula.apply(data);
  }

  @Override
  public Double[] value(List<Double> data) {
    return value(data.toArray(new Double[]{}));
  }

  @Override
  public void b(Double[] data) {
    b(x -> data);
  }

  @Override
  public double[] value(double[] doubles) throws IllegalArgumentException {
    return ArrayUtils.toPrimitive(value(ArrayUtils.toObject(doubles)));
  }
}
