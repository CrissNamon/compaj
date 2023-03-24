package tech.hiddenproject.compaj.gui.data;

import java.util.Objects;

public class Variable {

  private String name;
  private Object data;

  public Variable(String name, Object data) {
    this.name = name;
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Variable variable = (Variable) o;
    return name.equals(variable.name);
  }
}
