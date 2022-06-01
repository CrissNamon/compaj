package com.hiddenproject.compaj.gui.tab;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import com.hiddenproject.compaj.gui.*;
import com.hiddenproject.compaj.gui.util.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import org.kordamp.ikonli.carbonicons.*;
import org.kordamp.ikonli.javafx.*;
import org.reactfx.*;

public class EditorTab extends Tab {

  private static final String[] KEYWORDS = new String[]{
      "abstract", "assert", "boolean", "break", "byte",
      "case", "catch", "char", "class", "const",
      "continue", "default", "do", "double", "else",
      "enum", "extends", "final", "finally", "float",
      "for", "goto", "if", "implements", "import",
      "instanceof", "int", "interface", "long", "native",
      "new", "package", "private", "protected", "public",
      "return", "short", "static", "strictfp", "super",
      "switch", "synchronized", "this", "throw", "throws",
      "transient", "try", "void", "volatile", "while"
  };

  private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
  private static final String PAREN_PATTERN = "\\(|\\)";
  private static final String BRACE_PATTERN = "\\{|\\}";
  private static final String BRACKET_PATTERN = "\\[|\\]";
  private static final String SEMICOLON_PATTERN = "\\;";
  private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
  private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

  private static final Pattern PATTERN = Pattern.compile(
      "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
          + "|(?<PAREN>" + PAREN_PATTERN + ")"
          + "|(?<BRACE>" + BRACE_PATTERN + ")"
          + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
          + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
          + "|(?<STRING>" + STRING_PATTERN + ")"
          + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
  );


  private static final String LOG_INFO_PREFIX = "[INFO]";
  private static final String LOG_ERROR_PREFIX = "[ERROR]";

  private CodeArea codeArea;
  private ExecutorService executor;
  private TextArea consoleText;

  private File savedFile;
  private boolean unsavedChanges = false;

  public EditorTab() {
    super("Untitled.cjn");
    executor = Executors.newSingleThreadExecutor();
    BorderPane root = new BorderPane();
    ToolBar toolBar = new ToolBar();
    root.setTop(toolBar);
    SplitPane content = new SplitPane();
    root.setCenter(content);
    BorderPane editorContent = new BorderPane();
    codeArea = new CodeArea();
    codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    Subscription cleanupWhenDone = codeArea.multiPlainChanges()
        .successionEnds(Duration.ofMillis(500))
        .retainLatestUntilLater(executor)
        .supplyTask(this::computeHighlightingAsync)
        .awaitLatest(codeArea.multiPlainChanges())
        .filterMap(t -> {
          if (t.isSuccess()) {
            return Optional.of(t.get());
          } else {
            t.getFailure().printStackTrace();
            return Optional.empty();
          }
        })
        .subscribe(this::applyHighlighting);
    VBox.setVgrow(codeArea, Priority.ALWAYS);
    Accordion accordion = new Accordion();
    consoleText = new TextArea();
    TitledPane console = new TitledPane("Консоль", consoleText);
    console.setAnimated(false);
    editorContent.setCenter(codeArea);
    editorContent.setBottom(accordion);
    consoleText.setEditable(false);
    accordion.getPanes().add(console);
    content.getItems().add(editorContent);
    Button run = new Button();
    run.setGraphic(new FontIcon(CarbonIcons.PLAY_FILLED_ALT));
    Button save = new Button();
    save.setGraphic(new FontIcon(CarbonIcons.SAVE));
    Button open = new Button();
    open.setGraphic(new FontIcon(CarbonIcons.COPY_FILE));
    codeArea.textProperty().addListener(changeListener -> {
      if (getGraphic() == null) {
        setGraphic(new Label("*"));
      }
      unsavedChanges = true;
    });
    open.setOnAction(actionEvent -> {
      if (unsavedChanges) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("У вас есть несохраненные изменения");
        alert.setContentText("Открыть новый файл?");
        ButtonType cancelBtn = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okBtn = new ButtonType("Открыть", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(cancelBtn, okBtn);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
          return;
        }
      }
      File f = FileUtils.openFileWindow();
      savedFile = f;
      this.setText(f.getName());
      codeArea.clear();
      codeArea.replaceText(0, 0, FileUtils.readFile(f));
      setGraphic(null);
      unsavedChanges = false;
    });
    save.setOnAction(actionEvent -> {
      if (savedFile == null) {
        File file = FileUtils.saveFileWindow();
        savedFile = file;
      }
      if (savedFile != null) {
        FileUtils.saveFile(savedFile, codeArea.getText());
        this.setText(savedFile.getName());
        this.setGraphic(null);
        unsavedChanges = false;
      }
    });
    run.setOnAction(actionEvent -> {
      consoleText.clear();
      console.setExpanded(true);
      logInfo("Компиляция " + getText());
      try {
        Object result = Compaj.getTranslator().evaluate(codeArea.getText() + "\nprintln \"\"");
        log(result.toString());
        logInfo("OK");
      } catch (Exception e) {
        logError(e.getMessage());
        e.printStackTrace();
      }
    });
    toolBar.getItems().addAll(run, save, open);
    setContent(root);
    super.setOnClosed(event -> {
      executor.shutdownNow();
      cleanupWhenDone.unsubscribe();
    });
  }

  private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
    String text = codeArea.getText();
    Task<StyleSpans<Collection<String>>> task = new Task<>() {
      @Override
      protected StyleSpans<Collection<String>> call() {
        return computeHighlighting(text);
      }
    };
    executor.execute(task);
    return task;
  }

  private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
    codeArea.setStyleSpans(0, highlighting);
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

  private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    Matcher matcher = PATTERN.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder
        = new StyleSpansBuilder<>();
    while (matcher.find()) {
      String styleClass =
          matcher.group("KEYWORD") != null ? "keyword" :
              matcher.group("PAREN") != null ? "paren" :
                  matcher.group("BRACE") != null ? "brace" :
                      matcher.group("BRACKET") != null ? "bracket" :
                          matcher.group("SEMICOLON") != null ? "semicolon" :
                              matcher.group("STRING") != null ? "string" :
                                  matcher.group("COMMENT") != null ? "comment" :
                                      null; /* never happens */
      assert styleClass != null;
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastKwEnd = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    return spansBuilder.create();
  }

}
