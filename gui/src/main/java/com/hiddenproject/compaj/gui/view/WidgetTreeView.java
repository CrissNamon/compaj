package com.hiddenproject.compaj.gui.view;

import java.io.*;
import com.hiddenproject.compaj.gui.component.*;
import com.hiddenproject.compaj.gui.data.*;
import com.hiddenproject.compaj.gui.util.*;
import com.hiddenproject.compaj.gui.widget.*;
import javafx.scene.control.*;
import javafx.scene.paint.*;

public class WidgetTreeView extends TreeView<WorkSpaceWidget> {

  private TreeItem<WorkSpaceWidget> rootItem;

  public WidgetTreeView() {
    Label workSpaceDesc = new Label(I18nUtils.get("tab.workspace.desc"));
    BaseWidget rootWidget = new BaseWidget(workSpaceDesc, I18nUtils.get("tab.workspace.title"));
    rootItem = new TreeItem<>(rootWidget);
    rootItem.setExpanded(true);
    setRoot(rootItem);
    setShowRoot(true);
    getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    setCellFactory(workSpaceWidgetTreeView -> new WidgetTreeCell(this::treeCellUpdater));
  }

  private void treeCellUpdater(WidgetTreeCell treeCell) {
    if (! treeCell.isRootItem()) {
      ContextMenu contextMenu = new ContextMenuBuilder()
          .add(I18nUtils.get("alert.close"), i -> {
            treeCell.getWidget().close();
            treeCell.getParentItem().getChildren().remove(treeCell.getCellItem());
            getSelectionModel().clearSelection();
          })
          .add(I18nUtils.get("tab.workspace.widget.context.export"), i -> {
            try {
              File f = FileUtils.saveFileWindow("PNG Image", "*.png");
              SnapshotCreator.exportPngSnapshot(
                  treeCell.getWidget().getNode(),
                  f.toPath(),
                  Color.TRANSPARENT
              );
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
