package tech.hiddenproject.compaj.gui.suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

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
    String paragraph = text.substring(position - paragraphEnd, position)
        .replace("[.,()\t;=]", " ");
    int lastSpace = Math.max(paragraph.lastIndexOf(" "), 0);
    String prefix = paragraph.substring(lastSpace).trim();
    Set<Suggestion> suggestions = suggesters.stream()
        .flatMap(suggester -> suggester.predict(text, prefix, position).stream())
        .collect(Collectors.toSet());
    return new SuggestResult(suggestions, text);
  }

}
