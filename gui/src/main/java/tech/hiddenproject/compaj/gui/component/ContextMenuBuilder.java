package tech.hiddenproject.compaj.gui.component;

import java.util.function.Consumer;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ContextMenuBuilder {

  private final ContextMenu contextMenu;

  public ContextMenuBuilder() {
    contextMenu = new ContextMenu();
  }

  public ContextMenuBuilder(ContextMenu contextMenu) {
    this.contextMenu = contextMenu;
  }

  public ContextMenuBuilder add(String name, Consumer<MenuItem> event) {
    MenuItem menuItem = new MenuItem(name);
    menuItem.setOnAction(actionEvent -> event.accept(menuItem));
    contextMenu.getItems().add(menuItem);
    return this;
  }

  public ContextMenu build() {
    return contextMenu;
  }
}
