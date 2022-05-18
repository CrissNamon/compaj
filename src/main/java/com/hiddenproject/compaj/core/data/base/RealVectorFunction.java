package com.hiddenproject.compaj.core.data.base;

import java.util.Arrays;
import java.util.stream.IntStream;
import com.hiddenproject.compaj.core.data.NamedVectorFunction;
import com.hiddenproject.compaj.core.model.DynamicFunction;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class RealVectorFunction implements NamedVectorFunction<String, Number, Double> {

  private final String name;
  private DynamicFunction<Number, Double[]> formula;

  public RealVectorFunction(String name, Double[] initial) {
    this(name, (x) -> initial);
  }

  public RealVectorFunction(String name) {
    this(name, (d) -> new Double[]{});
  }

  public RealVectorFunction(String name, DynamicFunction<Number, Double[]> formula) {
    this.name = name;
    this.formula = formula;
  }

  @Override
  public double[] value(double[] doubles) throws IllegalArgumentException {
    return ArrayUtils.toPrimitive(formula.apply(ArrayUtils.toObject(doubles)));
  }

  @Override
  public DynamicFunction<Number, Double[]> getBinder() {
    return formula;
  }

  @Override
  public void b(DynamicFunction<Number, Double[]> formula) {
    this.formula = formula;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Double[] value(Number... data) {
    return formula.apply(data);
  }

  @Override
  public RealVector value(RealVector data) {
    return new ArrayRealVector(value(data.toArray()));
  }

  @Override
  public void b(Number[] data) {
    Double[] d = new Double[data.length];
    IntStream.range(0, data.length)
        .forEachOrdered(i -> d[i] = data[i].doubleValue());
    this.formula = (x) -> d;
  }
}
