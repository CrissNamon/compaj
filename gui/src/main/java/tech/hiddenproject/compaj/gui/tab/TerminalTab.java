package tech.hiddenproject.compaj.gui.tab;

import java.util.UUID;

import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.view.ReplView;
import tech.hiddenproject.compaj.gui.view.VariablesTableView;

public class TerminalTab extends CloseConfirmationTab {

  private static final String TAB_TERMINAL_TITLE = I18nUtils.get("tab.terminal.title");
  private static final String TAB_TERMINAL_REPL_CLEAR = I18nUtils.get("tab.terminal.repl.clear");

  private final ReplView replView;

  public TerminalTab() {
    super(TAB_TERMINAL_TITLE);
    setId(UUID.randomUUID().toString());
    Button clear = new Button(TAB_TERMINAL_REPL_CLEAR);
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
