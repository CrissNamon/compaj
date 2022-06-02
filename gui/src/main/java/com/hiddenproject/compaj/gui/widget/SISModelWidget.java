package com.hiddenproject.compaj.gui.widget;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.*;
import com.dlsc.formsfx.model.util.*;
import com.dlsc.formsfx.view.renderer.*;
import com.hiddenproject.compaj.applied.epidemic.*;
import com.hiddenproject.compaj.gui.component.*;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SISModelWidget implements WorkSpaceWidget {

  private final VBox root;
  private Consumer<WorkSpaceWidget> childAddedEvent = x -> {
  };
  private DoubleProperty alphaC;
  private DoubleProperty bettaC;
  private DoubleProperty s0;
  private DoubleProperty i0;
  private DoubleProperty t0;
  private DoubleProperty t1;
  private Form f;

  public SISModelWidget() {
    root = new VBox();
    alphaC = new SimpleDoubleProperty(0.04);
    bettaC = new SimpleDoubleProperty(0.4);
    s0 = new SimpleDoubleProperty(997);
    i0 = new SimpleDoubleProperty(3);
    t0 = new SimpleDoubleProperty(0);
    t1 = new SimpleDoubleProperty(100);
    f = Form.of(
        Group.of(
            Field.ofDoubleType(alphaC)
                .label((char) 945 + " = ")
                .tooltip("Коэффициент восстановления").required(true),
            Field.ofDoubleType(bettaC)
                .label((char) 946 + " = ")
                .tooltip("Коэффициент заражения")
                .required(true)
        ),
        Group.of(
            Field.ofDoubleType(s0)
                .label("S(0) = ")
                .required(true),
            Field.ofDoubleType(i0)
                .label("I(0) = ")
                .required(true)
        ),
        Group.of(
            Field.ofDoubleType(t0)
                .label("От")
                .required(true),
            Field.ofDoubleType(t1)
                .label("До")
                .required(true)
        )
    ).title("Модель SIR");
    FormRenderer formRenderer = new FormRenderer(f);
    Label name = new Label();
    name.setAlignment(Pos.BASELINE_CENTER);
    name.textProperty().bind(f.titleProperty());
    name.prefWidthProperty().bind(formRenderer.widthProperty());
    f.binding(BindingMode.CONTINUOUS);
    Button button = new Button("Вычислить");
    button.prefWidthProperty().bind(formRenderer.widthProperty());
    button.setAlignment(Pos.CENTER);
    button.disableProperty().bind(f.validProperty().not());
    button.setOnAction(this::calculateEvent);
    root.getChildren().addAll(name, formRenderer, button);
  }

  private void calculateEvent(ActionEvent actionEvent) {
    List<Number> timeSeries = IntStream.range((int) t0.get(), ((int) t1.get()))
        .asDoubleStream().boxed().collect(Collectors.toList());
    SISModel sisModel = new SISModel(s0.get(), i0.get());
    sisModel.withAlpha(alphaC.get());
    sisModel.withBetta(bettaC.get());
    sisModel.compute(t0.get(), t1.get());

    LineChartBuilder lineChartBuilder = new LineChartBuilder("Дни", "Люди", false);
    lineChartBuilder
        .with("S", timeSeries, sisModel.fnslog().get("S"))
        .with("I", timeSeries, sisModel.fnslog().get("I"));
    LineChart lineChart = lineChartBuilder.build();
    BaseWidget lineChartWidget = new BaseWidget(lineChart, "Линейная диаграмма");

    TableBuilder tableBuilder = new TableBuilder();
    tableBuilder.addColumn(0, "t", timeSeries)
        .addColumn(1, "S(t)", sisModel.fnslog().get("S"))
        .addColumn(2, "I(t)", sisModel.fnslog().get("I"));

    BaseWidget tableWidget = new BaseWidget(tableBuilder.build(), "Таблица данных");

    childAddedEvent.accept(lineChartWidget);
    childAddedEvent.accept(tableWidget);
  }

  @Override
  public void onChildAdded(Consumer<WorkSpaceWidget> event) {
    childAddedEvent = event;
  }

  @Override
  public Node getNode() {
    return root;
  }

  @Override
  public String toString() {
    return "Модель SIS { "
        + (char) 945
        + " = "
        + alphaC.get()
        + ", "
        + (char) 946
        + " = "
        + bettaC.get()
        + " }";
  }
}
