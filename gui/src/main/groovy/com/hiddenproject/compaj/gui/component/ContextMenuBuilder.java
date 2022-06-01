package com.hiddenproject.compaj.gui.component;

import java.util.function.*;
import javafx.scene.control.*;

public class ContextMenuBuilder {

  private ContextMenu contextMenu;

  public ContextMenuBuilder() {
    contextMenu = new ContextMenu();
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
