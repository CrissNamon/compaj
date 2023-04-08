package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Set;

/**
 * Result of {@link SuggestCore}.
 */
public class SuggestResult {

  private final Set<Suggestion> suggestions;
  private final String prefix;

  public SuggestResult(Set<Suggestion> suggestions, String prefix) {
    this.suggestions = suggestions;
    this.prefix = prefix;
  }

  /**
   * @return Found suggestions
   */
  public Set<Suggestion> getSuggestions() {
    return suggestions;
  }

  /**
   * @return Prefix for which suggestions are for
   */
  public String getPrefix() {
    return prefix;
  }
}
