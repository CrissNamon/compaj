package com.hiddenproject.compaj.core.data.base;

import java.util.Objects;
import com.hiddenproject.compaj.core.data.Constant;

public class BaseConstant implements Constant<String, Double> {

  private final Double data;
  private final String name;

  public BaseConstant(String name, Double data) {
    this.data = data;
    this.name = name;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BaseConstant that = (BaseConstant) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
