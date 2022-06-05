package tech.hiddenproject.compaj.gui.data;

import java.util.function.Consumer;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;

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
