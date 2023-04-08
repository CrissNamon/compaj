package tech.hiddenproject.compaj.gui.app;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.Objects;
import java.util.prefs.Preferences;

import tech.hiddenproject.aide.optional.IfTrueConditional;

public class AppSettings {

  public static final String APP_STYLESHEET = "/java-keywords.css";
  public static final String APP_TITLE = "CompaJ";
  private static final String APP_DIRECTORY = "CompaJ";
  private static final String SCRIPTS_DIRECTORY = "scripts";
  private static final String PLUGINS_DIRECTORY = "plugins";
  private static AppSettings INSTANCE;
  private final Preferences preferences;

  private AppSettings() {
    preferences = Preferences.userNodeForPackage(tech.hiddenproject.compaj.gui.Compaj.class);
  }

  public static AppSettings getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AppSettings();
    }
    return INSTANCE;
  }

  public String getStyleSheet() {
    URL url = getClass().getResource(APP_STYLESHEET);
    return IfTrueConditional.create()
        .ifTrue(Objects.nonNull(url)).then(() -> url.toExternalForm())
        .orElseThrows(() -> new RuntimeException("Stylesheet not found: " + APP_STYLESHEET));
  }

  public File getScriptsDirectoryFor(String project) {
    return createScriptsDirectory(createProjectDirectory(project));
  }

  private File createScriptsDirectory(File projectDir) {
    File scriptsDir = new File(projectDir.toString() + "/" + SCRIPTS_DIRECTORY);
    scriptsDir.mkdir();
    return scriptsDir;
  }

  public File createProjectDirectory(String name) {
    File dir = new File(getAppDirectory().toString() + "/" + name);
    dir.mkdir();
    return dir;
  }

  public File getAppDirectory() {
    String userHome = System.getProperty("user.home");
    File dir = new File(userHome + "/" + APP_DIRECTORY + "/");
    dir.mkdir();
    return dir;
  }

  public File getLibrariesDirectory() {
    File appDirectory = getAppDirectory();
    File libDirectory = new File(appDirectory.getAbsolutePath() + "/lib/");
    libDirectory.mkdir();
    return libDirectory;
  }

  public File getPluginsDirectory() {
    File appDirectory = getAppDirectory();
    File pluginsDir = new File(appDirectory.getAbsolutePath() + "/" + PLUGINS_DIRECTORY + "/");
    pluginsDir.mkdir();
    return pluginsDir;
  }

  public void put(AppPreference preference, String value) {
    preferences.put(preference.getName(), value);
  }

  public void put(AppPreference preference, Boolean value) {
    preferences.put(preference.getName(), String.valueOf(value));
  }

  public String get(AppPreference preference, String defaultValue) {
    return preferences.get(preference.getName(), defaultValue);
  }

  public Boolean get(AppPreference preference, Boolean defaultValue) {
    return Boolean.valueOf(preferences.get(preference.getName(), String.valueOf(defaultValue)));
  }

  public FileFilter pluginsFileFilter() {
    return pathname -> pathname.getName().endsWith(".jar");
  }
}
