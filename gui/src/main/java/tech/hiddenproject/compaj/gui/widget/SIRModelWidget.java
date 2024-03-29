package tech.hiddenproject.compaj.gui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.util.BindingMode;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tech.hiddenproject.compaj.applied.epidemic.SIRModel;
import tech.hiddenproject.compaj.gui.component.LineChartBuilder;
import tech.hiddenproject.compaj.gui.component.TableBuilder;

public class SIRModelWidget implements WorkSpaceWidget {

  private final VBox root;
  private Consumer<WorkSpaceWidget> childAddedEvent = x -> {
  };
  private DoubleProperty alphaC;
  private DoubleProperty bettaC;
  private DoubleProperty s0;
  private DoubleProperty i0;
  private DoubleProperty r0;
  private DoubleProperty t0;
  private DoubleProperty t1;
  private Form f;

  public SIRModelWidget() {
    root = new VBox();
    alphaC = new SimpleDoubleProperty(0.04);
    bettaC = new SimpleDoubleProperty(0.4);
    s0 = new SimpleDoubleProperty(997);
    i0 = new SimpleDoubleProperty(3);
    r0 = new SimpleDoubleProperty(0);
    t0 = new SimpleDoubleProperty(0);
    t1 = new SimpleDoubleProperty(100);
    f =
        Form.of(
                Group.of(
                    Field.ofDoubleType(alphaC)
                        .label((char) 945 + " = ")
                        .tooltip("Коэффициент восстановления")
                        .required(true),
                    Field.ofDoubleType(bettaC)
                        .label((char) 946 + " = ")
                        .tooltip("Коэффициент заражения")
                        .required(true)),
                Group.of(
                    Field.ofDoubleType(s0).label("S(0) = ").required(true),
                    Field.ofDoubleType(i0).label("I(0) = ").required(true),
                    Field.ofDoubleType(r0).label("R(0) = ").required(true)),
                Group.of(
                    Field.ofDoubleType(t0).label("От").required(true),
                    Field.ofDoubleType(t1).label("До").required(true)))
            .title("Модель SIR");
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
    List<Number> timeSeries =
        IntStream.range((int) t0.get(), ((int) t1.get()))
            .asDoubleStream()
            .boxed()
            .collect(Collectors.toList());
    SIRModel sirModel = new SIRModel(s0.get(), i0.get(), r0.get());
    sirModel.withAlpha(alphaC.get());
    sirModel.withBetta(bettaC.get());
    sirModel.compute(t0.get(), t1.get());

    LineChartBuilder lineChartBuilder = new LineChartBuilder("Дни", "Люди", false);
    lineChartBuilder
        .with("S", timeSeries, sirModel.fnslog().get("S"))
        .with("I", timeSeries, sirModel.fnslog().get("I"))
        .with("R", timeSeries, sirModel.fnslog().get("R"));
    javafx.scene.chart.LineChart lineChart = lineChartBuilder.build();
    lineChart.setAnimated(false);
    BaseWidget lineChartWidget = new BaseWidget(lineChart, "Линейная диаграмма");

    TableBuilder tableBuilder = new TableBuilder();
    tableBuilder
        .addColumn(0, "t", timeSeries)
        .addColumn(1, "S(t)", sirModel.fnslog().get("S"))
        .addColumn(2, "I(t)", sirModel.fnslog().get("I"))
        .addColumn(3, "R(t)", sirModel.fnslog().get("R"));

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
    return "Модель SIR { "
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
