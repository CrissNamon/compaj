package com.hiddenproject.compaj.gui.translator.data;

import java.util.ArrayList;
import java.util.List;

public class ClassOverrider {
  private String name;
  private String base;
  private String constructor;
  private List<MethodOverrider> methodOverriders;

  public ClassOverrider(String name, String base, String constructor, List<MethodOverrider> methodOverriders) {
    this.name = name;
    this.base = base;
    this.constructor = constructor;
    this.methodOverriders = methodOverriders;
  }

  public ClassOverrider(String name, String base, String constructor) {
    this(name, base, constructor, new ArrayList<>());
  }

  public String getName() {
    return name;
  }

  public String getBase() {
    return base;
  }

  public String getConstructor() {
    return constructor;
  }

  public void addMethodOverrider(MethodOverrider methodOverrider) {
    methodOverriders.add(methodOverrider);
  }

  public String constructClass() {
    StringBuilder c = new StringBuilder("class ")
        .append(name)
        .append(" extends ")
        .append(base)
        .append("{ ")
        .append(name)
        .append(constructor)
        .append("{ super")
        .append(constructor)
        .append(" }\n");
    for(MethodOverrider methodOverrider : methodOverriders) {
      c.append(methodOverrider.constructMethod());
      c.append("\n");
    }
    c.append("}\n");
    return c.toString();
  }
}