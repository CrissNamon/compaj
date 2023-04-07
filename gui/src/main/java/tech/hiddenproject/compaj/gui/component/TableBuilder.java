package tech.hiddenproject.compaj.gui.component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Builder for {@link TableView}.
 */
public class TableBuilder {

  private final TableView tableView;
  private final List<List> data;

  /**
   * Creates new builder.
   */
  public TableBuilder() {
    tableView = new TableView<>();
    data = new ArrayList<>();
  }

  /**
   * Adds new column to table.
   *
   * @param index Column index
   * @param name  Column name
   * @param data  Column data
   * @return {@link TableBuilder}
   */
  public TableBuilder addColumn(int index, String name, List data) {
    TableColumn<List<Object>, Object> column = new TableColumn<>(name);
    column.setCellValueFactory(
        cellData -> new SimpleObjectProperty<>(cellData.getValue().get(index)));
    tableView.getColumns().add(column);
    updateData(index, data);
    return this;
  }

  /**
   * Builds new {@link TableView}.
   *
   * @return {@link TableView}
   */
  public TableView build() {
    tableView.getItems().setAll(data);
    return tableView;
  }

  private void updateData(int index, List nData) {
    if (data.size() == 0) {
      IntStream.range(0, nData.size())
          .forEachOrdered(
              i -> {
                List l = new ArrayList();
                l.add(nData.get(i));
                data.add(l);
              });
    } else {
      IntStream.range(0, data.size()).forEachOrdered(i -> data.get(i).add(index, nData.get(i)));
    }
  }
}
