package tech.hiddenproject.compaj.gui.suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates suggestions for given text.
 */
public class SuggestCore {

  private final List<Suggester> suggesters = new ArrayList<>();

  /**
   * @param suggester {@link Suggester}
   */
  public void addSuggester(Suggester suggester) {
    this.suggesters.add(suggester);
  }

  /**
   * Collects suggestions from all {@link Suggester}.
   *
   * @param text     Text to suggestions are for
   * @param position Current caret position in text
   * @param paragraphEnd Current caret position in paragraph
   * @return {@link SuggestResult}
   */
  public SuggestResult predict(String text, int position, int paragraphEnd) {
    String paragraph = getParagraph(text, position, paragraphEnd);
    String prefix = getPrefix(paragraph);
    String preparedText = prepareText(text);
    CodeAnalyzer codeAnalyzer = CodeAnalyzer.create(preparedText);
    Set<Suggestion> suggestions = suggesters.stream()
        .flatMap(suggester -> suggester.predict(codeAnalyzer, prefix).stream())
        .collect(Collectors.toSet());
    return new SuggestResult(suggestions, text);
  }

  private String getParagraph(String text, int position, int paragraphEnd) {
    return text.substring(Math.max(position - paragraphEnd, 0), position)
        .replaceAll("[,\t;=]", " ").replaceAll("\\(.*?\\)(?!\")", "");
  }

  private String getPrefix(String paragraph) {
    int lastSpace = Math.max(paragraph.lastIndexOf(" "), 0);
    return paragraph.substring(lastSpace).trim();
  }

  private String prepareText(String text) {
    return text.replaceAll("\\s", " ").replaceAll("\\(.*?\\)(?!\")", "");
  }

}
