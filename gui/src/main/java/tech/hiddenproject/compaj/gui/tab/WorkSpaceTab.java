package tech.hiddenproject.compaj.gui.tab;

import java.util.UUID;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.view.WidgetTreeView;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;

public class WorkSpaceTab extends CloseConfirmationTab {

  private final WidgetTreeView widgetTreeView;
  private final ScrollPane widgetPane;
  private static final String TAB_WORKSPACE_TITLE = "tab.workspace.title";

  public WorkSpaceTab() {
    super(I18nUtils.get(TAB_WORKSPACE_TITLE));
    setClosable(false);

    widgetPane = new ScrollPane();
    widgetPane.setFitToWidth(true);
    widgetPane.setFitToHeight(true);
    setId(UUID.randomUUID().toString());

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
