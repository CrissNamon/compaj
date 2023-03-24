package tech.hiddenproject.compaj.gui.view;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

public class CodeAreaView extends CodeArea {

  private static final String[] KEYWORDS =
      new String[]{
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

  private static final String PAREN_PATTERN = "\\(|\\)";
  private static final String BRACE_PATTERN = "\\{|\\}";
  private static final String BRACKET_PATTERN = "\\[|\\]";
  private static final String SEMICOLON_PATTERN = "\\;";
  private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
  private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

  private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
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
  private ExecutorService executor;
  private Subscription syntaxHighlight;

  public CodeAreaView() {
    executor = Executors.newSingleThreadExecutor();
    syntaxHighlight = subscribeOnSyntaxHighlight();
    setParagraphGraphicFactory(LineNumberFactory.get(this));
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
