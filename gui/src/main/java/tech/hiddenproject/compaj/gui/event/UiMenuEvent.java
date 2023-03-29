package tech.hiddenproject.compaj.gui.event;

import javafx.scene.control.Menu;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;

public class UiMenuEvent extends CompaJEvent {

  /**
   * Called when all root menus are ready.
   */
  public static final String STARTUP_NAME = "UI_MENU_STARTUP";
  public static final String ADD_ROOT_NAME = "UI_MENU_ADD_ROOT";
  public static final String ADD_CHILD_NAME = "UI_MENU_ADD_CHILD";

  public static final UiMenuEvent STARTUP = new UiMenuEvent(STARTUP_NAME, null);

  public UiMenuEvent(String name, Object payload) {
    super(name, payload);
  }

  public static UiMenuEvent ADD_ROOT(Menu menu) {
    return new UiMenuEvent(ADD_ROOT_NAME, menu);
  }

  public static UiMenuEvent ADD_CHILD(UiChildPayload payload) {
    return new UiMenuEvent(ADD_CHILD_NAME, payload);
  }
}
