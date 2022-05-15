package com.hiddenproject.compaj.core.data.base;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.hiddenproject.compaj.core.data.Variable;

public class BaseVariable implements Variable<String, Double> {

  private final String name;
  private Double data;
  private Supplier<Double> formula;

  public BaseVariable(String name, Double initial) {
    this.name = name;
    this.data = initial;
    this.formula = () -> initial;
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
  public void s(Double data) {
    this.data = data;
  }

  public void s(int data) {
    this.data = (double) data;
  }

  @Override
  public void b(Supplier<Double> formula) {
    this.formula = formula;
  }

  @Override
  public Double g() {
    return getBinder().get();
  }

  @Override
  public String toString() {
    return "BaseVariable{" +
        "name='" + name + '\'' +
        ", data=" + data +
        ", formula=" + formula +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseVariable that = (BaseVariable) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


}
