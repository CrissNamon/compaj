package com.hiddenproject.compaj.gui.translator.data;

public class MethodOverrider {
  private String name;
  private String returnType;
  private String body;

  public MethodOverrider(String name, String returnType, String body) {
    this.name = name;
    this.returnType = returnType;
    this.body = body;
  }

  public String getName() {
    return name;
  }

  public String getReturnType() {
    return returnType;
  }

  public String getBody() {
    return body;
  }

  public String constructMethod() {
    return getReturnType() + " " + getName() + getBody();
  }
}
