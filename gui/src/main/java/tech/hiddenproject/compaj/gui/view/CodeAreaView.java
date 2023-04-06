package tech.hiddenproject.compaj.gui.view;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import tech.hiddenproject.compaj.gui.AppPreference;
import tech.hiddenproject.compaj.gui.suggestion.KeywordSuggester;
import tech.hiddenproject.compaj.gui.suggestion.SuggestCore;
import tech.hiddenproject.compaj.gui.suggestion.SuggestResult;
import tech.hiddenproject.compaj.gui.suggestion.VariableMethodsSuggester;
import tech.hiddenproject.compaj.gui.suggestion.VariableNameSuggester;

public class CodeAreaView extends CodeArea {

  private static final String PAREN_PATTERN = "\\(|\\)";
  private static final String BRACE_PATTERN = "\\{|\\}";
  private static final String BRACKET_PATTERN = "\\[|\\]";
  private static final String SEMICOLON_PATTERN = "\\;";
  private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
  private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

  private static final String KEYWORD_PATTERN =
      "\\b(" + String.join("|", KeywordSuggester.KEYWORDS) + ")\\b";
  private static final Pattern PATTERN =
      Pattern.compile(
          "(?<KEYWORD>"
              + KEYWORD_PATTERN
              + ")"
              + "|(?<PAREN>"
              + PAREN_PATTERN
              + ")"
              + "|(?<BRACE>"
              + BRACE_PATTERN
              + ")"
              + "|(?<BRACKET>"
              + BRACKET_PATTERN
              + ")"
              + "|(?<SEMICOLON>"
              + SEMICOLON_PATTERN
              + ")"
              + "|(?<STRING>"
              + STRING_PATTERN
              + ")"
              + "|(?<COMMENT>"
              + COMMENT_PATTERN
              + ")");
  private final ExecutorService executor;
  private final Subscription syntaxHighlight;
  private final ContextMenu autoCompleteSuggestions = SuggestContextMenu.create().build();

  public CodeAreaView() {
    executor = Executors.newSingleThreadExecutor();
    syntaxHighlight = subscribeOnSyntaxHighlight();
    setParagraphGraphicFactory(LineNumberFactory.get(this));

    KeywordSuggester keywordSuggester = new KeywordSuggester();
    VariableNameSuggester variableNameSuggester = new VariableNameSuggester();
    VariableMethodsSuggester variableMethodsSuggester = new VariableMethodsSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableMethodsSuggester);
    suggestCore.addSuggester(variableNameSuggester);
    suggestCore.addSuggester(keywordSuggester);
    onMouseClickedProperty().set(mouseEvent -> {
      if (!mouseEvent.getTarget().equals(autoCompleteSuggestions)) {
        autoCompleteSuggestions.hide();
        return;
      }
      mouseEvent.consume();
    });
    onKeyReleasedProperty().set(keyEvent -> autoCompleteSuggest(keyEvent, suggestCore));
  }

  private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    Matcher matcher = PATTERN.matcher(text);
    int lastKwEnd = 0;
    StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    while (matcher.find()) {
      String styleClass =
          matcher.group("KEYWORD") != null
              ? "keyword"
              : matcher.group("PAREN") != null
                  ? "paren"
                  : matcher.group("BRACE") != null
                      ? "brace"
                      : matcher.group("BRACKET") != null
                          ? "bracket"
                          : matcher.group("SEMICOLON") != null
                              ? "semicolon"
                              : matcher.group("STRING") != null
                                  ? "string"
                                  : matcher.group("COMMENT") != null
                                      ? "comment"
                                      : null; /* never happens */
      assert styleClass != null;
      spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
      spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
      lastKwEnd = matcher.end();
    }
    spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
    return spansBuilder.create();
  }

  private void autoCompleteSuggest(KeyEvent keyEvent, SuggestCore suggestCore) {
    if (!AppPreference.CODE_AUTOCOMPLETE.getOrDefault(true)) {
      return;
    }
    KeyCode keyCode = keyEvent.getCode();
    if (!keyCode.isLetterKey() && !keyCode.isDigitKey()
        && !keyCode.equals(KeyCode.PERIOD) && !keyCode.equals(KeyCode.BACK_SPACE)) {
      return;
    }
    if (getCaretBounds().isEmpty()) {
      return;
    }
    autoCompleteSuggestions.getItems().clear();
    Bounds bounds = getCaretBounds().get();
    SuggestResult suggestResult = suggestCore.predict(getText(), getCaretPosition());
    Set<String> suggestions = suggestResult.getSuggestions();
    suggestions.forEach(
        suggestion -> autoCompleteSuggestions.getItems().add(new MenuItem(suggestion)));
    autoCompleteSuggestions.setOnAction(actionEvent -> {
      MenuItem menuItem = (MenuItem) actionEvent.getTarget();
      String oneLine = getText().replace("\n", " ").substring(0, getCaretPosition());
      int lastSpace = oneLine.lastIndexOf(" ");
      int lastDot = oneLine.lastIndexOf(".");
      int lastTab = oneLine.lastIndexOf("\t");
      replaceText(Math.max(Math.max(lastDot, lastSpace), lastTab) + 1, getCaretPosition(),
                  menuItem.getText());
    });
    autoCompleteSuggestions.show(CodeAreaView.this, bounds.getCenterX(),
                                 bounds.getCenterY() + 10);
  }

  private Subscription subscribeOnSyntaxHighlight() {
    return multiPlainChanges()
        .successionEnds(Duration.ofMillis(500))
        .retainLatestUntilLater(executor)
        .supplyTask(this::computeHighlightingAsync)
        .awaitLatest(multiPlainChanges())
        .filterMap(
            t -> {
              if (t.isSuccess()) {
                return Optional.of(t.get());
              } else {
                t.getFailure().printStackTrace();
                return Optional.empty();
              }
            })
        .subscribe(this::applyHighlighting);
  }

  private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
    String text = getText();
    Task<StyleSpans<Collection<String>>> task =
        new Task<>() {
          @Override
          protected StyleSpans<Collection<String>> call() {
            return computeHighlighting(text);
          }
        };
    executor.execute(task);
    return task;
  }

  private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
    setStyleSpans(0, highlighting);
  }

  public void onClose() {
    executor.shutdownNow();
    syntaxHighlight.unsubscribe();
  }
}
