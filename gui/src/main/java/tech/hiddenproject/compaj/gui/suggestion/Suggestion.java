package tech.hiddenproject.compaj.gui.suggestion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.FieldJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;

/**
 * Represents suggestion for code completion.
 */
public class Suggestion {

  private final String suggestionText;
  private final String javadoc;

  public Suggestion(String suggestionText) {
    this.suggestionText = suggestionText;
    this.javadoc = "";
  }

  public Suggestion(Method method) {
    this.suggestionText = CodeAnalyzer.createMethodName(method);
    MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
    this.javadoc = methodJavadoc.getComment() + "\n" + methodJavadoc.getParams().stream()
        .map(paramJavadoc -> paramJavadoc.getName() + ": " + paramJavadoc.getComment())
        .reduce("", (s1, s2) -> s1 + s2);
  }

  public Suggestion(Field field) {
    this.suggestionText = field.getName();
    FieldJavadoc fieldJavadoc = RuntimeJavadoc.getJavadoc(field);
    this.javadoc = fieldJavadoc.getComment().toString();
  }

  public Suggestion(Class<?> c) {
    this.suggestionText = c.getSimpleName();
    ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(c);
    this.javadoc = classJavadoc.getComment().toString();
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
