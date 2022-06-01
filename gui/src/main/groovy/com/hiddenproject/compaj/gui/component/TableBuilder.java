package com.hiddenproject.compaj.gui.component;


import java.util.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.scene.control.*;

public class TableBuilder {

  private TableView tableView;
  private List<List> data;

  public TableBuilder() {
    tableView = new TableView<>();
    data = new ArrayList<>();
  }

  public TableBuilder addColumn(int index, String name, List data) {
    TableColumn<List<Object>, Object> column = new TableColumn<>(name);
    column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(index)));
    tableView.getColumns().add(column);
    updateData(index, data);
    return this;
  }

  private void updateData(int index, List nData) {
    if (data.size() == 0) {
      IntStream.range(0, nData.size())
          .forEachOrdered(i -> {
            List l = new ArrayList();
            l.add(nData.get(i));
            data.add(l);
          });
    } else {
      IntStream.range(0, data.size())
          .forEachOrdered(i -> data.get(i).add(index, nData.get(i)));
    }
  }

  public TableView build() {
    tableView.getItems().setAll(data);
    return tableView;
  }

}
