package tech.hiddenproject.compaj.gui.component;

import java.util.function.Consumer;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Builder for {@link ContextMenu}.
 */
public class ContextMenuBuilder {

  private final ContextMenu contextMenu;

  /**
   * Creates new builder with standard {@link ContextMenu}.
   */
  public ContextMenuBuilder() {
    contextMenu = new ContextMenu();
  }

  /**
   * Creates new builder with given {@link ContextMenu}.
   *
   * @param contextMenu {@link ContextMenu}
   */
  public ContextMenuBuilder(ContextMenu contextMenu) {
    this.contextMenu = contextMenu;
  }

  /**
   * Adds new {@link MenuItem} to menu.
   *
   * @param name  Item name
   * @param event {@link Consumer} for {@link MenuItem}
   * @return {@link ContextMenuBuilder}
   */
  public ContextMenuBuilder add(String name, Consumer<MenuItem> event) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.setOnAction(actionEvent -> event.accept(menuItem));
    contextMenu.getItems().add(menuItem);
    return this;
  }

  /**
   * Builds {@link ContextMenu}.
   *
   * @return {@link ContextMenu}
   */
  public ContextMenu build() {
    return contextMenu;
  }
}
