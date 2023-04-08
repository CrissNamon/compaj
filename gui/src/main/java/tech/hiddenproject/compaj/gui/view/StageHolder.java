package tech.hiddenproject.compaj.gui.view;

import java.util.Objects;

import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import tech.hiddenproject.aide.optional.BooleanOptional;

public class StageHolder {

  private static StageHolder INSTANCE;

  private final Stage stage;

  private final TabPane content = new TabPane();

  private StageHolder(Stage stage) {
    this.stage = stage;
  }

  public static synchronized StageHolder getInstance() {
    BooleanOptional.of(Objects.isNull(INSTANCE))
        .ifTrueThrow(() -> new RuntimeException("Main stage is not set!"));
    return INSTANCE;
  }

  public static synchronized StageHolder createInstance(Stage stage) {
    if (Objects.isNull(INSTANCE)) {
      INSTANCE = new StageHolder(stage);
    }
    return INSTANCE;
  }

  public Stage getStage() {
    return stage;
  }

  public TabPane getContent() {
    return content;
  }
}
