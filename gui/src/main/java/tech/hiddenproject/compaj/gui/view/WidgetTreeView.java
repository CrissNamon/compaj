package tech.hiddenproject.compaj.gui.view;

import java.io.File;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import tech.hiddenproject.compaj.gui.component.AlertBuilder;
import tech.hiddenproject.compaj.gui.component.ContextMenuBuilder;
import tech.hiddenproject.compaj.gui.data.WidgetTreeCell;
import tech.hiddenproject.compaj.gui.util.FileUtils;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.util.SnapshotCreator;
import tech.hiddenproject.compaj.gui.widget.BaseWidget;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;

public class WidgetTreeView extends TreeView<WorkSpaceWidget> {

  private TreeItem<WorkSpaceWidget> rootItem;

  private static final String TAB_WORKSPACE_DESC = I18nUtils.get("tab.workspace.desc");
  private static final String TAB_WORKSPACE_TITLE = I18nUtils.get("tab.workspace.title");
  private static final String ALERT_CLOSE = I18nUtils.get("alert.close");
  private static final String TAB_WORKSPACE_WIDGET_CONTEXT_EXPORT = I18nUtils.get("tab.workspace.widget.context.export");

  public WidgetTreeView() {
    Label workSpaceDesc = new Label(TAB_WORKSPACE_DESC);
    BaseWidget rootWidget = new BaseWidget(workSpaceDesc, TAB_WORKSPACE_TITLE);
    rootItem = new TreeItem<>(rootWidget);
    rootItem.setExpanded(true);
    setRoot(rootItem);
    setShowRoot(true);
    getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    setCellFactory(workSpaceWidgetTreeView -> new WidgetTreeCell(this::treeCellUpdater));
  }

  private void treeCellUpdater(WidgetTreeCell treeCell) {
    if (!treeCell.isRootItem()) {
      ContextMenu contextMenu =
          new ContextMenuBuilder()
              .add(
                  ALERT_CLOSE,
                  i -> {
                    treeCell.getWidget().close();
                    treeCell.getParentItem().getChildren().remove(treeCell.getCellItem());
                    getSelectionModel().clearSelection();
                  })
              .add(
                  TAB_WORKSPACE_WIDGET_CONTEXT_EXPORT,
                  i -> {
                    try {
                      File f = FileUtils.saveFileWindow("PNG Image", "*.png");
                      SnapshotCreator.exportPngSnapshot(
                          treeCell.getWidget().getNode(), f.toPath(), Color.TRANSPARENT);
                    } catch (IOException e) {
                      new AlertBuilder("Внимание", Alert.AlertType.ERROR)
                          .header("Ошибка")
                          .content(e.getLocalizedMessage())
                          .build()
                          .show();
                    }
                  })
              .build();
      treeCell.setContextMenu(contextMenu);
    }
  }

  public void addItem(WorkSpaceWidget workSpaceWidget) {
    TreeItem<WorkSpaceWidget> item = new TreeItem(workSpaceWidget);
    item.setExpanded(true);
    workSpaceWidget.onChildAdded(child -> item.getChildren().add(new TreeItem<>(child)));
    rootItem.getChildren().add(item);
  }

  public TreeItem<WorkSpaceWidget> getRootItem() {
    return rootItem;
  }
}
