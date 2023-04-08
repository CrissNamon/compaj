package tech.hiddenproject.compaj.gui.suggestion;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;

public class Suggestion {

  private final String suggestion;
  private final String javadoc;

  public Suggestion(String suggestion) {
    this.suggestion = suggestion;
    this.javadoc = "";
  }

  public Suggestion(String suggestion, MethodJavadoc javadoc) {
    this.suggestion = suggestion;
    this.javadoc = javadoc.getComment() + "\n" + javadoc.getParams().stream()
        .map(paramJavadoc -> paramJavadoc.getName() + ": " + paramJavadoc.getComment())
        .reduce("", (s1, s2) -> s1 + s2);
  }

  public Suggestion(String suggestion, FieldJavadoc javadoc) {
    this.suggestion = suggestion;
    this.javadoc = javadoc.getComment().toString();
  }

  public Suggestion(String suggestion, ClassJavadoc javadoc) {
    this.suggestion = suggestion;
    this.javadoc = javadoc.getComment().toString();
  }

  public String getSuggestion() {
    return suggestion;
  }

  public String getJavadoc() {
    return javadoc;
  }
}
