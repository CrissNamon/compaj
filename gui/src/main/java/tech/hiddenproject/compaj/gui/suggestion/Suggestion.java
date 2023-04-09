package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Objects;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;

public class Suggestion {

  private final String suggestionText;
  private final String javadoc;

  public Suggestion(String suggestionText) {
    this.suggestionText = suggestionText;
    this.javadoc = "";
  }

  public Suggestion(String suggestionText, MethodJavadoc javadoc) {
    this.suggestionText = suggestionText;
    this.javadoc = javadoc.getComment() + "\n" + javadoc.getParams().stream()
        .map(paramJavadoc -> paramJavadoc.getName() + ": " + paramJavadoc.getComment())
        .reduce("", (s1, s2) -> s1 + s2);
  }

  public Suggestion(String suggestionText, FieldJavadoc javadoc) {
    this.suggestionText = suggestionText;
    this.javadoc = javadoc.getComment().toString();
  }

  public Suggestion(String suggestionText, ClassJavadoc javadoc) {
    this.suggestionText = suggestionText;
    this.javadoc = javadoc.getComment().toString();
  }

  public String getSuggestion() {
    return suggestionText;
  }

  public String getJavadoc() {
    return javadoc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Suggestion that = (Suggestion) o;
    return suggestionText.equals(that.suggestionText) && javadoc.equals(that.javadoc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suggestionText, javadoc);
  }

  @Override
  public String toString() {
    return "Suggestion{" +
        "suggestionText='" + suggestionText + '\'' +
        '}';
  }
}
