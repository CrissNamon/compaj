package tech.hiddenproject.compaj.gui.suggestion;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link Suggester} for variables names.
 */
public class VariableNameSuggester implements Suggester {

  @Override
  public Set<Suggestion> predict(CodeAnalyzer codeAnalyzer, String prefix) {
    if (prefix.contains(".")) {
      return new HashSet<>();
    }
    return Stream.of(codeAnalyzer.getVariables().prefixMap(prefix),
                     codeAnalyzer.getClasses().prefixMap(prefix))
        .flatMap(tree -> tree.keySet().stream())
        .map(Suggestion::new)
        .collect(Collectors.toSet());
  }

}
