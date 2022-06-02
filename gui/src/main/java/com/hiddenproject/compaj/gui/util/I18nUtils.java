package com.hiddenproject.compaj.gui.util;

import java.util.*;

public class I18nUtils {

  private static Locale currentLocale;
  private static ResourceBundle currentBundle;

  static {
    currentLocale = Locale.getDefault();
    try {
      currentBundle = ResourceBundle.getBundle("lang", currentLocale);
    } catch (MissingResourceException e) {
      currentLocale = Locale.ROOT;
      currentBundle = ResourceBundle.getBundle("lang", currentLocale);
    }
  }

  public static String get(String key) {
    return get(currentLocale, key);
  }

  public static String get(Locale curLoc, String key) {
    return currentBundle.getString(key);

  }

  public static void setCurrentLocale(Locale locale) {
    currentLocale = locale;
  }

}
