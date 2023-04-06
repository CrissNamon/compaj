package tech.hiddenproject.compaj.gui;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.hiddenproject.compaj.gui.suggestion.KeywordSuggester;
import tech.hiddenproject.compaj.gui.suggestion.SuggestCore;
import tech.hiddenproject.compaj.gui.suggestion.VariableMethodsSuggester;
import tech.hiddenproject.compaj.gui.suggestion.VariableNameSuggester;

@DisplayName("SuggestCoreTest should > ")
class SuggestCoreTest {

  private static final String CODE = "class MyClass {\n"
      + "  void function(args) {\n"
      + "      }\n"
      + " }\n"
      + "s = \"Hello world\"\n"
      + "int x = 5\n"
      + "myVariable = s\n";

  @DisplayName("suggest names of exist variables")
  @Test
  void suggestExistVariableNameTest() {
    VariableNameSuggester variableNameSuggester = new VariableNameSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableNameSuggester);
    String text = CODE + "myV";

    Set<String> expected = Set.of("myVariable");
    Set<String> actual = suggestCore.predict(text, text.length()).getSuggestions();

    Assertions.assertIterableEquals(expected, actual);
  }

  @DisplayName("suggest nothing for non existing variable")
  @Test
  void suggestNotExistVariableNameTest() {
    VariableNameSuggester variableNameSuggester = new VariableNameSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableNameSuggester);
    String text = CODE + "notE";

    Set<String> expected = Set.of();
    Set<String> actual = suggestCore.predict(text, text.length()).getSuggestions();

    Assertions.assertIterableEquals(expected, actual);
  }

  @DisplayName("suggest existing keyword")
  @Test
  void suggestExistingKeyWordsTest() {
    KeywordSuggester keywordSuggester = new KeywordSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(keywordSuggester);

    String text = "syn";
    Set<String> expected = new HashSet<>(Set.of("synchronized"));
    Set<String> actual = suggestCore.predict(text, text.length()).getSuggestions();

    Assertions.assertIterableEquals(expected, actual);
  }

  @DisplayName("suggest methods of variable")
  @Test
  void suggestVariableMethodTest() {
    VariableMethodsSuggester variableMethodsSuggester = new VariableMethodsSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableMethodsSuggester);

    String text = CODE + "myVariable.valueO";
    Set<String> expected = Set.of("valueOf(class java.lang.Object)", "valueOf(boolean)",
                                  "valueOf(class (C, int, int)", "valueOf(int)", "valueOf(float)",
                                  "valueOf(class (C)", "valueOf(double)", "valueOf(long)",
                                  "valueOf(char)");
    Set<String> actual = suggestCore.predict(text, text.length()).getSuggestions();

    Assertions.assertTrue(expected.containsAll(actual));
  }

  @DisplayName("suggest static method of class")
  @Test
  void suggestClassStaticMethodTest() {
    VariableMethodsSuggester variableMethodsSuggester = new VariableMethodsSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableMethodsSuggester);

    String text = "String.mat";
    Set<String> expected = Set.of("matches(class java.lang.String)");
    Set<String> actual = suggestCore.predict(text, text.length()).getSuggestions();

    Assertions.assertTrue(expected.containsAll(actual));
  }

}
