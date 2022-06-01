package com.hiddenproject.compaj.gui.tab;

import com.hiddenproject.compaj.gui.component.*;
import com.hiddenproject.compaj.gui.widget.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class WorkSpaceTab extends Tab {

  private TreeItem<WorkSpaceWidget> rootItem;

  public WorkSpaceTab() {
    super("Рабочее пространство");
    SplitPane root = new SplitPane();
    BorderPane widgetPane = new BorderPane();
    TreeView<WorkSpaceWidget> widgetTree = new TreeView<>();
    widgetTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    widgetTree.setCellFactory(workSpaceWidgetTreeView -> new TreeCell<>() {
      @Override
      protected void updateItem(WorkSpaceWidget workSpaceWidget, boolean b) {
        super.updateItem(workSpaceWidget, b);
        if (workSpaceWidget == null || isEmpty()) {
          this.setGraphic(null);
        } else {
          this.setGraphic(new Label(workSpaceWidget.toString()));
          TreeItem child = this.getTreeItem();
          TreeItem parent = child.getParent();
          if (parent == null) {
            return;
          }
          ContextMenu contextMenu = new ContextMenuBuilder()
              .add("Закрыть", i -> {
                workSpaceWidget.close();
                parent.getChildren().remove(child);
                widgetTree.getSelectionModel().clearSelection();
              })
              .build();
          this.setContextMenu(contextMenu);
        }
      }
    });
    root.setDividerPosition(0, 0.4);
    Label workSpaceDesc = new Label("Это рабочее пространство. Здесь будут появляться все визуальные представления");
    BaseWidget rootWidget = new BaseWidget(workSpaceDesc, "Рабочее пространство");
    rootItem = new TreeItem<>(rootWidget);
    rootItem.setExpanded(true);
    widgetTree.setRoot(rootItem);
    widgetTree.setShowRoot(true);
    root.getItems().addAll(widgetTree, widgetPane);
    setContent(root);
    widgetTree.getSelectionModel().selectedItemProperty()
        .addListener((observableValue, workSpaceWidgetTreeItem, t1) -> {
          if (workSpaceWidgetTreeItem != t1 && t1 != null) {
            widgetPane.setCenter(t1.getValue().getNode());
            t1.getValue().show();
          }
        });
    setOnClosed(event -> rootItem.getChildren().forEach(c -> c.getValue().close()));
  }

  public void addItem(WorkSpaceWidget workSpaceWidget) {
    TreeItem<WorkSpaceWidget> item = new TreeItem(workSpaceWidget);
    item.setExpanded(true);
    workSpaceWidget.onChildAdded(child -> item.getChildren().add(new TreeItem<>(child)));
    rootItem.getChildren().add(item);
  }

}
