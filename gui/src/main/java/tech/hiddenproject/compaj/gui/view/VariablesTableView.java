package tech.hiddenproject.compaj.gui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import tech.hiddenproject.compaj.gui.Compaj;
import tech.hiddenproject.compaj.gui.data.Variable;
import tech.hiddenproject.compaj.gui.util.I18nUtils;

public class VariablesTableView extends VBox {

  private TableView<Variable> root;
  private List<Variable> data;
  private static final String TAB_TERMINAL_VARIABLES_VARIABLE = "tab.terminal.variables.variable";
  private static final String TAB_TERMINAL_VARIABLES_VALUE = "tab.terminal.variables.value";

  public VariablesTableView() {
    data = new ArrayList<>();
    root = new TableView<>();
    root.prefHeightProperty().bind(heightProperty());
    root.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    TableColumn<Variable, String> nameColumn =
        new TableColumn<>(I18nUtils.get(TAB_TERMINAL_VARIABLES_VARIABLE));
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    TableColumn<Variable, Object> dataColumn =
        new TableColumn<>(I18nUtils.get(TAB_TERMINAL_VARIABLES_VALUE));
    dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
    root.getColumns().addAll(nameColumn, dataColumn);
    root.getItems().addAll(data);
    getChildren().add(root);
  }

  public void addVariable(Variable v) {
    root.getItems().add(v);
  }

  public void update() {
    Map<String, Object> variables = Compaj.getInstance().getTranslator().getVariables();
    for (Map.Entry<String, Object> entry : variables.entrySet()) {
      if (Compaj.getInstance().getTranslator().getHiddenVariables().contains(entry.getKey())) {
        continue;
      }
      Variable v = new Variable(entry.getKey(), entry.getValue().toString());
      int i = root.getItems().indexOf(v);
      if (i == -1) {
        root.getItems().add(v);
      } else {
        root.getItems().get(i).setData(entry.getValue().toString());
      }
    }
    root.refresh();
  }
}
