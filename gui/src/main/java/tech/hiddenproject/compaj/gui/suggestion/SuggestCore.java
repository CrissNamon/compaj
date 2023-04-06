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
   * @param text Text to suggestions are for
   * @param position Current caret position in text
   * @return {@link SuggestResult}
   */
  public SuggestResult predict(String text, int position) {
    String preparedText = text.replace("\n", " ").replaceAll("\\(.*?\\)", "");
    int newPosition = position - (text.length() - preparedText.length());
    int lastSpace = preparedText.substring(0, newPosition).lastIndexOf(" ");
    String prefix = (lastSpace > -1 ? preparedText.substring(lastSpace + 1, newPosition)
        : preparedText).trim();
    Set<String> suggestions = suggesters.stream()
        .flatMap(suggester -> suggester.predict(preparedText, prefix, position).stream())
        .collect(Collectors.toSet());
    return new SuggestResult(suggestions, prefix);
  }

}
