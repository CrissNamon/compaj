package com.hiddenproject.compaj.core.data.base;

import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.model.*;
import org.apache.commons.lang3.*;
import org.apache.commons.math3.analysis.*;

import java.util.*;

public class RealFunction implements NamedFunction<String, Double, Double>, MultivariateFunction {

  protected final String name;
  protected DynamicFunction<Double, Double> formula;

  public RealFunction(String name, Double initial) {
    this(name, (x) -> initial);
  }

  public RealFunction(String name) {
    this(name, (d) -> 0d);
  }

  public RealFunction(String name, DynamicFunction<Double, Double> f) {
    this.name = name;
    b(f);
  }

  @Override
  public DynamicFunction<Double, Double> getBinder() {
    return this.formula;
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
  public Double value(Double[] data) {
    return getBinder().apply(data);
  }

  @Override
  public Double value(List<Double> data) {
    return value(data.toArray(data.toArray(new Double[0])));
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
  public double value(double[] doubles) {
    return value(ArrayUtils.toObject(doubles));
  }
}
