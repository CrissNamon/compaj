package tech.hiddenproject.compaj.gui;

public enum AppPreference {
  LANGUAGE("LANG");

  private String name;

  AppPreference(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
