package com.hiddenproject.compaj.core.data.base;

import java.util.*;
import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.model.*;
import org.apache.commons.lang3.*;
import org.apache.commons.math3.analysis.*;

public class RealVectorFunction implements NamedFunction<String, Double, Double[]>, MultivariateVectorFunction {

  protected final String name;
  protected DynamicFunction<Double, Double[]> formula;

  public RealVectorFunction(String name, Double[] initial) {
    this(name, (x) -> initial);
  }

  public RealVectorFunction(String name, DynamicFunction<Double, Double[]> f) {
    this.name = name;
    this.formula = f;
  }

  public RealVectorFunction(String name) {
    this(name, (d) -> new Double[0]);
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
