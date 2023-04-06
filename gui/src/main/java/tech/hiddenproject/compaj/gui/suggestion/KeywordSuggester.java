package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Suggester} for keywords
 */
public class KeywordSuggester implements Suggester {

  public static final Set<String> KEYWORDS =
      Set.of(
          "abstract", "assert", "boolean", "break", "byte",
          "case", "catch", "char", "class", "const",
          "continue", "default", "do", "double", "else",
          "enum", "extends", "final", "finally", "float",
          "for", "goto", "if", "implements", "import",
          "instanceof", "int", "interface", "long", "native",
          "new", "package", "private", "protected", "public",
          "return", "short", "static", "strictfp", "super",
          "switch", "synchronized", "this", "throw", "throws",
          "transient", "try", "void", "volatile", "while", "trait"
      );

  @Override
  public Set<String> predict(String input, String prefix, int position) {
    return KEYWORDS.stream()
        .filter(keyword -> keyword.startsWith(prefix))
        .collect(Collectors.toSet());
  }
}
