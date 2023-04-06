package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Set;

/**
 * Result of {@link SuggestCore}.
 */
public class SuggestResult {

  private final Set<String> suggestions;
  private final String prefix;

  public SuggestResult(Set<String> suggestions, String prefix) {
    this.suggestions = suggestions;
    this.prefix = prefix;
  }

  /**
   * @return Found suggestions
   */
  public Set<String> getSuggestions() {
    return suggestions;
  }

  /**
   * @return Prefix for which suggestions are for
   */
  public String getPrefix() {
    return prefix;
  }
}
