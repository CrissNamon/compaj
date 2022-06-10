package tech.hiddenproject.compaj.gui.tab;

import java.io.File;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.carbonicons.CarbonIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import tech.hiddenproject.compaj.gui.Compaj;
import tech.hiddenproject.compaj.gui.component.AlertBuilder;
import tech.hiddenproject.compaj.gui.util.FileUtils;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.view.CodeAreaView;

public class EditorTab extends Tab {

  private static final String LOG_INFO_PREFIX = "[INFO]";
  private static final String LOG_ERROR_PREFIX = "[ERROR]";

  private final CodeAreaView codeArea;
  private final TextArea consoleText;
  private final TitledPane console;

  private File savedFile;
  private boolean unsavedChanges = false;

  public EditorTab() {
    super("Untitled.cjn");

    /* ToolBar Creation */
    Button run = new Button();
    run.setGraphic(new FontIcon(CarbonIcons.PLAY_FILLED_ALT));
    Button save = new Button();
    save.setGraphic(new FontIcon(CarbonIcons.SAVE));
    Button open = new Button();
    open.setGraphic(new FontIcon(CarbonIcons.COPY_FILE));
    open.setOnAction(this::openFileAction);
    save.setOnAction(this::saveFileAction);
    run.setOnAction(this::runFileAction);
    ToolBar toolBar = new ToolBar();
    toolBar.getItems().addAll(run, save, open);

    /* Code Editor creation */
    codeArea = new CodeAreaView();
    VBox.setVgrow(codeArea, Priority.ALWAYS);
    consoleText = new TextArea();
    console = new TitledPane(I18nUtils.get("tab.editor.console.title"), consoleText);
    console.setAnimated(false);
    console.setExpanded(false);
    consoleText.setEditable(false);
    codeArea.textProperty().addListener(this::codeChangeListener);

    BorderPane editorContent = new BorderPane();
    editorContent.setCenter(codeArea);
    editorContent.setBottom(console);

    BorderPane root = new BorderPane();
    root.setTop(toolBar);
    root.setCenter(editorContent);
    setContent(root);
    setOnClosed(this::onClose);
  }

  private void openFileAction(Event event) {
    if (unsavedChanges) {
      Alert alert =
          new AlertBuilder(I18nUtils.get("alert.confirm"), Alert.AlertType.CONFIRMATION)
              .header(I18nUtils.get("alert.unsaved.header"))
              .content(I18nUtils.get("alert.unsaved.content"))
              .clearButtonTypes()
              .button(I18nUtils.get("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE)
              .button(I18nUtils.get("alert.ok"), ButtonBar.ButtonData.OK_DONE)
              .build();
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
        return;
      }
    }
    File f = FileUtils.openNoteWindow();
    savedFile = f;
    this.setText(f.getName());
    codeArea.clear();
    codeArea.replaceText(0, 0, FileUtils.readFile(f));
    setGraphic(null);
    unsavedChanges = false;
  }

  private void saveFileAction(Event event) {
    if (savedFile == null) {
      savedFile = FileUtils.saveNoteWindow();
    }
    if (savedFile != null) {
      FileUtils.saveFile(savedFile, codeArea.getText());
      this.setText(savedFile.getName());
      this.setGraphic(null);
      unsavedChanges = false;
    }
  }

  private void runFileAction(Event event) {
    consoleText.clear();
    console.setExpanded(true);
    logInfo(I18nUtils.get("tab.editor.console.compilation") + " " + getText());
    try {
      Object result = Compaj.getTranslator().evaluate(codeArea.getText() + "\nprintln \"\"");
      log(result.toString());
      logInfo(I18nUtils.get("tab.editor.console.compilation.ok"));
    } catch (Exception e) {
      logError(e.getMessage());
      e.printStackTrace();
    }
  }

  private void codeChangeListener(Observable observable) {
    if (getGraphic() == null) {
      setGraphic(new Label("*"));
    }
    unsavedChanges = true;
  }

  private void onClose(Event event) {
    codeArea.onClose();
  }

  private void logInfo(String text) {
    consoleText.appendText(LOG_INFO_PREFIX + " " + text + "\n");
  }

  private void log(String text) {
    consoleText.appendText(text + "\n");
  }

  private void logError(String text) {
    consoleText.appendText(LOG_ERROR_PREFIX + " " + text + "\n");
  }
}