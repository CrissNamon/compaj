package tech.hiddenproject.compaj.gui.widget;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tech.hiddenproject.compaj.applied.epidemic.SIRABModel;
import tech.hiddenproject.compaj.core.data.EnvironmentObject;
import tech.hiddenproject.compaj.core.data.base.GridAgent;
import tech.hiddenproject.compaj.core.data.base.GridLocation;

public class SIRABModelWidget implements WorkSpaceWidget {

  private VBox root;
  private Canvas canvas;
  private GraphicsContext graphicsContext2D;
  private GridLocation canvasCenter;

  private SIRABModel sirabModel;

  private ScheduledExecutorService scheduledExecutorService;
  private ScheduledFuture scheduledFuture;
  private AtomicInteger iteration;

  private SimpleStringProperty iterationLabelText;
  private SimpleBooleanProperty canGoBack;
  private SimpleStringProperty autoButtonText;
  private boolean isAuto;

  public SIRABModelWidget(SIRABModel sirabModel) {
    iteration = new AtomicInteger(-1);
    root = new VBox();
    canvas = new Canvas();
    canvas.setHeight(sirabModel.model().getHeight());
    canvas.setWidth(sirabModel.model().getWidth() + 50);
    canvasCenter = new GridLocation(canvas.getWidth() / 2, canvas.getHeight() / 2);
    this.sirabModel = sirabModel;
    graphicsContext2D = canvas.getGraphicsContext2D();
    HBox controls = new HBox();
    HBox.setHgrow(controls, Priority.ALWAYS);
    controls.setAlignment(Pos.BASELINE_CENTER);
    Button previous = new Button("<");
    canGoBack = new SimpleBooleanProperty(iteration.get() <= 0);
    previous.disableProperty().bind(canGoBack);
    previous.setOnAction(this::prevStep);
    Button next = new Button(">");
    next.setOnAction(this::nextStep);
    Label iterationLabel = new Label();
    iterationLabelText = new SimpleStringProperty("Шаг: " + iteration.get());
    iterationLabel.textProperty().bind(iterationLabelText);
    autoButtonText = new SimpleStringProperty("Автоматически");
    Button button = new Button();
    button.textProperty().bind(autoButtonText);
    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    button.setOnAction(this::auto);
    controls.getChildren().addAll(previous, iterationLabel, next, button);
    root.getChildren().addAll(controls, canvas);
  }

  private void prevStep(ActionEvent actionEvent) {
    animate(iteration.decrementAndGet());
  }

  private void nextStep(ActionEvent actionEvent) {
    animate(iteration.incrementAndGet());
  }

  private void auto(ActionEvent actionEvent) {
    if (isAuto) {
      isAuto = false;
      scheduledFuture.cancel(true);
      //scheduledExecutorService.shutdownNow();
      autoButtonText.setValue("Автоматически");
      return;
    }
    autoButtonText.setValue("Пауза");
    isAuto = true;
    scheduledFuture =
        scheduledExecutorService.scheduleAtFixedRate(
            () -> animate(iteration.incrementAndGet()),
            1000,
            50,
            TimeUnit.MILLISECONDS);
  }

  private void animate(int step) {
    Platform.runLater(
        () -> {
          iterationLabelText.setValue("Шаг: " + iteration.get());
          canGoBack.setValue(iteration.get() <= 0);
          sirabModel.model().step();
          graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
          graphicsContext2D.setFill(Color.valueOf("#eb9b34"));
          graphicsContext2D.fillOval(canvas.getWidth() - 45, 20, 6, 6);
          graphicsContext2D.setFill(Color.valueOf("#eb4034"));
          graphicsContext2D.fillOval(canvas.getWidth() - 45, 40, 6, 6);
          graphicsContext2D.setFill(Color.valueOf("#34eb40"));
          graphicsContext2D.fillOval(canvas.getWidth() - 45, 60, 6, 6);
          graphicsContext2D.setFill(Color.BLACK);
          graphicsContext2D.fillText("S", canvas.getWidth() - 20, 25);
          graphicsContext2D.fillText("I", canvas.getWidth() - 20, 45);
          graphicsContext2D.fillText("R", canvas.getWidth() - 20, 65);
          graphicsContext2D.setStroke(Color.valueOf("#4334eb"));
          graphicsContext2D.setLineWidth(5);
          graphicsContext2D.strokeRect(0, 0, canvas.getWidth() - 52, canvas.getHeight());
          for (EnvironmentObject<GridLocation> object : sirabModel.model().environmentObjects()) {
            double[] x = new double[object.getPoints().size()];
            double[] y = new double[object.getPoints().size()];
            for (int i = 0; i < object.getPoints().size(); ++i) {
              x[i] = Math.abs(object.getPoints().get(i).getX() + canvasCenter.getX());
              y[i] = Math.abs(object.getPoints().get(i).getY() - canvasCenter.getY());
            }
            graphicsContext2D.strokePolygon(x, y, object.getPoints().size());
          }
          for (GridAgent a : sirabModel.model().getHistory().get(step)) {
            if (a.getGroup().equals("S")) {
              graphicsContext2D.setFill(Color.valueOf("#eb9b34"));
            }
            if (a.getGroup().equals("I")) {
              graphicsContext2D.setFill(Color.valueOf("#eb4034"));
            }
            if (a.getGroup().equals("R")) {
              graphicsContext2D.setFill(Color.valueOf("#34eb40"));
            }
            graphicsContext2D.fillOval(
                Math.abs(a.getLocation().getX() + canvas.getWidth() / 2),
                Math.abs(a.getLocation().getY() - canvas.getHeight() / 2),
                6,
                6);
          }
        });
  }

  @Override
  public void onChildAdded(Consumer<WorkSpaceWidget> event) {
  }

  @Override
  public Node getNode() {
    return root;
  }

  @Override
  public void close() {
    scheduledExecutorService.shutdownNow();
  }

  @Override
  public String toString() {
    return "SIRAB Симуляция";
  }
}
