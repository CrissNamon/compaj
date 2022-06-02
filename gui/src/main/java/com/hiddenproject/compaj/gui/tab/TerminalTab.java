package com.hiddenproject.compaj.gui.tab;

import com.hiddenproject.compaj.gui.util.*;
import com.hiddenproject.compaj.gui.view.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class TerminalTab extends Tab {

  private final ReplView replView;

  public TerminalTab() {
    super(I18nUtils.get("tab.terminal.title"));
    Button clear = new Button(I18nUtils.get("tab.terminal.repl.clear"));
    clear.setOnAction(actionEvent -> clear());
    ToolBar toolBar = new ToolBar();
    toolBar.getItems().add(clear);

    replView = new ReplView();
    VariablesTableView variablesTableView = new VariablesTableView();
    replView.onCommandEvaluated(r -> variablesTableView.update());
    SplitPane content = new SplitPane();
    content.getItems().addAll(replView, variablesTableView);

    BorderPane root = new BorderPane();
    root.setTop(toolBar);
    root.setCenter(content);
    setContent(root);
  }

  public void clear() {
    replView.clearHistory();
  }

}
