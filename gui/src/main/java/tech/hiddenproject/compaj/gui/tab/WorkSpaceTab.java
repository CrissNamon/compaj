package tech.hiddenproject.compaj.gui.tab;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.view.WidgetTreeView;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;

public class WorkSpaceTab extends Tab {

  private final WidgetTreeView widgetTreeView;
  private final ScrollPane widgetPane;

  public WorkSpaceTab() {
    super(I18nUtils.get("tab.workspace.title"));

    widgetPane = new ScrollPane();
    widgetPane.setFitToWidth(true);
    widgetPane.setFitToHeight(true);

    widgetTreeView = new WidgetTreeView();
    widgetTreeView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(this::widgetTreeChangeListener);
    setOnClosed(
        event -> widgetTreeView.getRootItem().getChildren().forEach(c -> c.getValue().close()));

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
