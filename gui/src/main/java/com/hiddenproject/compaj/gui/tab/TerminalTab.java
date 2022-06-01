package com.hiddenproject.compaj.gui.tab;

import com.hiddenproject.compaj.gui.view.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class TerminalTab extends Tab {

  private ReplView replView;

  public TerminalTab() {
    super("Терминал");
    BorderPane root = new BorderPane();
    SplitPane content = new SplitPane();
    ToolBar toolBar = new ToolBar();
    Button clear = new Button("Очистить");
    clear.setOnAction(actionEvent -> clear());
    toolBar.getItems().add(clear);
    replView = new ReplView();
    VariablesTableView variablesTableView = new VariablesTableView();
    replView.onCommandEvaluated(r -> variablesTableView.update());
    content.getItems().addAll(replView, variablesTableView);
    root.setTop(toolBar);
    root.setCenter(content);
    setContent(root);
  }

  public void clear() {
    replView.clearHistory();
  }

}
