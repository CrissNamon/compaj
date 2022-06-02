package com.hiddenproject.compaj.gui.data;

import java.util.function.*;
import com.hiddenproject.compaj.gui.widget.*;
import javafx.scene.control.*;

public class WidgetTreeCell extends TreeCell<WorkSpaceWidget> {

  private final Consumer<WidgetTreeCell> updateEvent;
  private WorkSpaceWidget widget;

  public WidgetTreeCell() {
    this(null);
  }

  public WidgetTreeCell(Consumer<WidgetTreeCell> onUpdate) {
    super();
    updateEvent = onUpdate;
  }

  @Override
  protected void updateItem(WorkSpaceWidget workSpaceWidget, boolean b) {
    super.updateItem(workSpaceWidget, b);
    if (workSpaceWidget == null || isEmpty()) {
      this.setGraphic(null);
    } else {
      widget = workSpaceWidget;
      this.setGraphic(new Label(workSpaceWidget.toString()));
      if (updateEvent != null) {
        updateEvent.accept(this);
      }
    }
  }

  public boolean isRootItem() {
    return getParentItem() == null;
  }

  public TreeItem<WorkSpaceWidget> getParentItem() {
    return getTreeItem().getParent();
  }

  public TreeItem<WorkSpaceWidget> getCellItem() {
    return getTreeItem();
  }

  public WorkSpaceWidget getWidget() {
    return widget;
  }
}
