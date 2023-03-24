package tech.hiddenproject.compaj.lang.data;

public class MethodOverrider {

  private final String name;
  private final String returnType;
  private final String body;

  public MethodOverrider(String name, String returnType, String body) {
    this.name = name;
    this.returnType = returnType;
    this.body = body;
  }

  public String constructMethod() {
    return getReturnType() + " " + getName() + getBody();
  }

  public String getReturnType() {
    return returnType;
  }

  public String getName() {
    return name;
  }

  public String getBody() {
    return body;
  }
}
