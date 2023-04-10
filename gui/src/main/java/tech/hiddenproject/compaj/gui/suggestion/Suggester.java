package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Set;

public interface Suggester {

  /**
   * Searches for {@link Suggestion} based on prefix and {@link CodeAnalyzer}.
   *
   * @param codeAnalyzer {@link CodeAnalyzer}
   * @param prefix Prefix to create suggestions for
   * @return {@link Suggestion}
   */
  Set<Suggestion> predict(CodeAnalyzer codeAnalyzer, String prefix);

}
