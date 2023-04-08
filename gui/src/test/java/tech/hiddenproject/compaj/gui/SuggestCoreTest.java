package tech.hiddenproject.compaj.gui;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.hiddenproject.compaj.gui.suggestion.KeywordSuggester;
import tech.hiddenproject.compaj.gui.suggestion.SuggestCore;
import tech.hiddenproject.compaj.gui.suggestion.Suggestion;
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
    String suggestFor = "myV";
    String text = CODE + suggestFor;

    Set<Suggestion> expected = Set.of(new Suggestion("myVariable"));
    Set<Suggestion> actual = suggestCore.predict(text, text.length(), suggestFor.length()).getSuggestions();

    Assertions.assertIterableEquals(expected, actual);
  }

  @DisplayName("suggest nothing for non existing variable")
  @Test
  void suggestNotExistVariableNameTest() {
    VariableNameSuggester variableNameSuggester = new VariableNameSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableNameSuggester);
    String suggestFor = "notE";
    String text = CODE + suggestFor;

    Set<String> expected = Set.of();
    Set<Suggestion> actual = suggestCore.predict(text, text.length(), suggestFor.length()).getSuggestions();

    Assertions.assertIterableEquals(expected, actual);
  }

  @DisplayName("suggest existing keyword")
  @Test
  void suggestExistingKeyWordsTest() {
    KeywordSuggester keywordSuggester = new KeywordSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(keywordSuggester);

    String text = "syn";
    Set<Suggestion> expected = Set.of(new Suggestion("synchronized"));
    Set<Suggestion> actual = suggestCore.predict(text, text.length(), text.length()).getSuggestions();

    Assertions.assertIterableEquals(expected, actual);
  }

  @DisplayName("suggest methods of variable")
  @Test
  void suggestVariableMethodTest() {
    VariableMethodsSuggester variableMethodsSuggester = new VariableMethodsSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableMethodsSuggester);

    String suggestFor = "myVariable.valueO";
    String text = CODE + suggestFor;
    Set<Suggestion> expected = Set.of(new Suggestion("valueOf(class java.lang.Object)"),
                                      new Suggestion("valueOf(boolean)"),
                                      new Suggestion("valueOf(class (C, int, int)"),
                                      new Suggestion("valueOf(int)"),
                                      new Suggestion("valueOf(float)"),
                                      new Suggestion("valueOf(class (C)"),
                                      new Suggestion("valueOf(double)"),
                                      new Suggestion("valueOf(long)"),
                                      new Suggestion("valueOf(char)"));
    Set<Suggestion> actual = suggestCore.predict(text, text.length(), suggestFor.length()).getSuggestions();

    Assertions.assertTrue(expected.containsAll(actual));
  }

  @DisplayName("suggest static method of class")
  @Test
  void suggestClassStaticMethodTest() {
    VariableMethodsSuggester variableMethodsSuggester = new VariableMethodsSuggester();
    SuggestCore suggestCore = new SuggestCore();
    suggestCore.addSuggester(variableMethodsSuggester);

    String text = "String.mat";
    Set<Suggestion> expected = Set.of(
        new Suggestion("matches(class java.lang.String)"));
    Set<Suggestion> actual = suggestCore.predict(text, text.length(), text.length()).getSuggestions();

    Assertions.assertTrue(expected.containsAll(actual));
  }

}
