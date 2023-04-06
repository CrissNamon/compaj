package tech.hiddenproject.compaj.gui;

/**
 * Contains app preferences.
 */
public enum AppPreference {
  /**
   * App language
   */
  LANGUAGE("LANG"),
  /**
   * Code completion feature.
   */
  CODE_AUTOCOMPLETE("CODE_AUTOCOMPLETE");

  private final String name;

  AppPreference(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getOrDefault(String defaultValue) {
    return AppSettings.getInstance().get(this, defaultValue);
  }

  public Boolean getOrDefault(Boolean defaultValue) {
    return Boolean.valueOf(AppSettings.getInstance()
                               .get(this, String.valueOf(defaultValue)));
  }

  public void update(String value) {
    AppSettings.getInstance().put(this, value);
  }

  public void update(Boolean value) {
    AppSettings.getInstance().put(this, String.valueOf(value));
  }
}
