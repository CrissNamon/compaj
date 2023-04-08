package tech.hiddenproject.compaj.gui.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class MenuHolder {

  private static MenuHolder INSTANCE;

  private final Map<String, Menu> menus = new HashMap<>();
  private final MenuBar menuBar = new MenuBar();

  private MenuHolder() {
    final String os = System.getProperty("os.name");
    if (os != null && os.startsWith("Mac")) {
      menuBar.useSystemMenuBarProperty().set(true);
    }
  }

  public static synchronized MenuHolder getInstance() {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new MenuHolder();
    }
    return INSTANCE;
  }

  public MenuBar getMenuBar() {
    return menuBar;
  }

  public void addRootMenu(Menu... menus) {
    menuBar.getMenus().addAll(menus);
  }

  public boolean contains(String key) {
    return menus.containsKey(key);
  }

  public void put(String key, Menu menu) {
    menus.put(key, menu);
  }

  public Menu get(String key) {
    return menus.get(key);
  }
}
