package com.hiddenproject.compaj.core.data.base;

import java.util.function.Supplier;
import com.hiddenproject.compaj.core.data.Variable;

public class BaseVariable implements Variable<String, Double> {

  private String name;
  private Double data;
  private Supplier<Double> formula;

  public BaseVariable(String name, Double initial) {
    this.name = name;
    this.data = initial;
    this.formula = () -> 0.0;
  }

  public BaseVariable(String name, Double initial, Supplier<Double> formula) {
    this(name, initial);
    this.formula = formula;
  }

  @Override
  public Supplier<Double> getBinder() {
    return formula;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Double getData() {
    return data;
  }

  @Override
  public void setData(Double data) {
    this.data = data;
  }

  @Override
  public void bind(Supplier<Double> formula) {
    this.formula = formula;
  }
}
