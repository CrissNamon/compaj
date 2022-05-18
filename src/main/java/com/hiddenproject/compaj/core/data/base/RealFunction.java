package com.hiddenproject.compaj.core.data.base;

import java.util.Objects;
import com.hiddenproject.compaj.core.data.NamedFunction;
import com.hiddenproject.compaj.core.model.DynamicFunction;
import groovy.lang.Delegate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.linear.RealVector;

public class RealFunction implements NamedFunction<String, Double, Double>, MultivariateFunction {

  private final String name;
  private DynamicFunction<Double, Double> formula;

  public RealFunction(String name, Double initial) {
    this(name, (x) -> initial);
  }

  public RealFunction(String name) {
    this(name, (d) -> 0d);
  }

  public RealFunction(String name, DynamicFunction<Double, Double> formula) {
    this.name = name;
    this.formula = formula;
  }

  @Override
  public DynamicFunction<Double, Double> getBinder() {
    return formula;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void b(DynamicFunction<Double, Double> formula) {
    this.formula = formula;
  }

  @Override
  public Double value(Double... data) {
    return getBinder().apply(data);
  }

  public Double value(RealVector data) {
    return value(data.toArray());
  }

  @Override
  public void b(Double data) {
    this.formula = (x) -> data;
  }

  @Override
  public String toString() {
    return "RealFunction{" +
        "name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RealFunction that = (RealFunction) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public double value(double[] doubles) {
    return value(ArrayUtils.toObject(doubles));
  }
}
