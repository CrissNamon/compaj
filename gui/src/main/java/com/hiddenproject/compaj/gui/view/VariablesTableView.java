package com.hiddenproject.compaj.gui.view;

import java.util.*;
import com.hiddenproject.compaj.gui.*;
import com.hiddenproject.compaj.gui.data.*;
import com.hiddenproject.compaj.gui.util.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;

public class VariablesTableView extends VBox {

  private TableView<Variable> root;
  private List<Variable> data;

  public VariablesTableView() {
    data = new ArrayList<>();
    root = new TableView<>();
    root.prefHeightProperty().bind(heightProperty());
    root.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    TableColumn<Variable, String> nameColumn = new TableColumn<>(
        I18nUtils.get("tab.terminal.variables.variable")
    );
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    TableColumn<Variable, Object> dataColumn = new TableColumn<>(
        I18nUtils.get("tab.terminal.variables.value")
    );
    dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
    root.getColumns().addAll(nameColumn, dataColumn);
    root.getItems().addAll(data);
    getChildren().add(root);
  }

  public void addVariable(Variable v) {
    root.getItems().add(v);
  }

  public void update() {
    Map<String, Object> variables = Compaj.getTranslator().getVariables();
    for (Map.Entry<String, Object> entry : variables.entrySet()) {
      if (entry.getKey().equals("out")) {
        continue;
      }
      Variable v = new Variable(entry.getKey(), entry.getValue().toString());
      int i = root.getItems().indexOf(v);
      if (i == - 1) {
        root.getItems().add(v);
      } else {
        root.getItems().get(i).setData(entry.getValue().toString());
      }
    }
    root.refresh();
  }

}
