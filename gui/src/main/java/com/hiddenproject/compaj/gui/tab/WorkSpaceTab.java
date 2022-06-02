package com.hiddenproject.compaj.gui.tab;

import com.hiddenproject.compaj.gui.util.*;
import com.hiddenproject.compaj.gui.view.*;
import com.hiddenproject.compaj.gui.widget.*;
import javafx.beans.value.*;
import javafx.scene.control.*;

public class WorkSpaceTab extends Tab {

  private final WidgetTreeView widgetTreeView;
  private final ScrollPane widgetPane;

  public WorkSpaceTab() {
    super(I18nUtils.get("tab.workspace.title"));

    widgetPane = new ScrollPane();
    widgetPane.setFitToWidth(true);
    widgetPane.setFitToHeight(true);

    widgetTreeView = new WidgetTreeView();
    widgetTreeView.getSelectionModel().selectedItemProperty()
        .addListener(this::widgetTreeChangeListener);
    setOnClosed(event -> widgetTreeView.getRootItem()
        .getChildren().forEach(c -> c.getValue().close())
    );

    SplitPane root = new SplitPane();
    root.setDividerPosition(0, 0.4);
    root.getItems().addAll(widgetTreeView, widgetPane);

    setContent(root);
  }

  private void widgetTreeChangeListener(
      ObservableValue<? extends TreeItem<WorkSpaceWidget>> observableValue,
      TreeItem<WorkSpaceWidget> workSpaceWidgetTreeItem,
      TreeItem<WorkSpaceWidget> t1) {
    if (workSpaceWidgetTreeItem != t1 && t1 != null) {
      widgetPane.setContent(t1.getValue().getNode());
      t1.getValue().show();
    }
  }

  public void addItem(WorkSpaceWidget workSpaceWidget) {
    widgetTreeView.addItem(workSpaceWidget);
  }

}
