package tech.hiddenproject.compaj.gui.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import tech.hiddenproject.compaj.gui.app.AppPreference;
import tech.hiddenproject.compaj.gui.app.AppSettings;
import tech.hiddenproject.compaj.gui.component.AlertBuilder;

public class I18nUtils {

  private static final ResourceBundle.Control FALLBACK =
      ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
  private static final String RESOURCE_NAME = "lang";
  private static final String ALERT_INFO = "alert.info";
  private static final String MENU_SETTINGS_LAND_CHANGE = "menu.settings.lang.change";
  private static Locale currentLocale;
  private static ResourceBundle currentBundle;

  static {
    String lang =
        AppSettings.getInstance().get(AppPreference.LANGUAGE, Locale.getDefault().toLanguageTag());
    currentLocale = Locale.forLanguageTag(lang);
    loadBundle();
  }

  public static void changeLang(Locale locale) {
    AppSettings.getInstance().put(AppPreference.LANGUAGE, locale.toLanguageTag());
    currentLocale = locale;
    loadBundle();
    clearCache();
    new AlertBuilder(I18nUtils.get(ALERT_INFO), Alert.AlertType.INFORMATION)
        .content(I18nUtils.get(MENU_SETTINGS_LAND_CHANGE))
        .build()
        .show();
  }

  private static void loadBundle() {
    try {
      currentBundle = ResourceBundle.getBundle(RESOURCE_NAME, currentLocale, FALLBACK);
    } catch (MissingResourceException e) {
      currentLocale = Locale.ROOT;
      currentBundle = ResourceBundle.getBundle(RESOURCE_NAME, currentLocale, FALLBACK);
    }
  }

  public static void clearCache() {
    ResourceBundle.clearCache();
  }

  public static String get(String key) {
    return get(currentLocale, key);
  }

  private static String get(Locale curLoc, String key) {
    return currentBundle.getString(key);
  }
}
