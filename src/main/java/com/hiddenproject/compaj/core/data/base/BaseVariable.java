package com.hiddenproject.compaj.core.data.base;

import java.util.Objects;
import com.hiddenproject.compaj.core.data.Variable;

public class BaseVariable implements Variable<String, Double> {

  private Double data;
  private final String name;

  public BaseVariable(String name, Double data) {
    this.data = data;
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Double g() {
    return data;
  }

  @Override
  public void b(Double data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "BaseVariable{" +
        "data=" + data +
        ", name='" + name + '\'' +
        '}';
  }

  public <D extends Number> Double plus(D d) {
    return d.doubleValue() + g();
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
