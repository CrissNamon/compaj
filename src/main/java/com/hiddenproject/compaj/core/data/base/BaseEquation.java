package com.hiddenproject.compaj.core.data.base;

import java.util.Objects;
import java.util.function.Supplier;
import com.hiddenproject.compaj.core.data.Equation;

public class BaseEquation implements Equation<String, Double> {

  private final String name;
  private Supplier<Number> formula;

  public BaseEquation(String name, Double initial) {
    this(name, () -> initial);
  }

  public BaseEquation(String name) {
    this(name, () -> 0d);
  }

  public BaseEquation(String name, Supplier<Number> formula) {
    this.name = name;
    this.formula = formula;
  }

  @Override
  public Supplier<Number> getBinder() {
    return formula;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void b(Supplier<Number> formula) {
    this.formula = formula;
  }

  @Override
  public Double g() {
    return getBinder().get().doubleValue();
  }

  @Override
  public void b(Double data) {
    this.formula = () -> data;
  }

  @Override
  public String toString() {
    return "BaseEquation{" +
        "name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseEquation that = (BaseEquation) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


}
