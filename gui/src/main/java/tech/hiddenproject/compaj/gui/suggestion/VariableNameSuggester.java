package tech.hiddenproject.compaj.gui.suggestion;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {@link Suggester} for variables names.
 */
public class VariableNameSuggester implements Suggester {

  private static final String VAR_GROUP = "VAR";
  private static final String PATTERN_UNFORMATTED = "(?<%s>\\w+)[ \r\n\t]*=";
  private static final Pattern PATTERN =
      Pattern.compile(String.format(PATTERN_UNFORMATTED, VAR_GROUP));

  @Override
  public Set<String> predict(String text, String prefix, int position) {
    Set<String> variables = analyze(text);
    if (prefix.contains(".")) {
      return new HashSet<>();
    }
    return variables.parallelStream()
        .filter(name -> name.startsWith(prefix))
        .collect(Collectors.toSet());
  }

  private Set<String> analyze(String text) {
    Matcher matcher = PATTERN.matcher(text);
    Set<String> variables = new HashSet<>();
    while (matcher.find()) {
      if (Objects.nonNull(matcher.group(VAR_GROUP))) {
        variables.add(matcher.group(VAR_GROUP));
      }
    }
    return variables;
  }
}
