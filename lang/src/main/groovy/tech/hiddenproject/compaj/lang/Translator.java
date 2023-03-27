package tech.hiddenproject.compaj.lang;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

/**
 * Represents translator for some lang.
 */
public interface Translator {

  /**
   * Evaluates given script.
   *
   * @param script Script to evaluate
   * @return Evaluation result
   */
  Object evaluate(String script);

  /**
   * Evaluates given script.
   *
   * @param script           Script to evaluate
   * @param codeTranslations Set of {@link CodeTranslation} to use for translation process.
   * @return Evaluation result
   */
  Object evaluate(String script, Set<CodeTranslation> codeTranslations);

  /**
   * @return {@link TranslatorUtils}
   */
  TranslatorUtils getTranslatorUtils();

  /**
   * @return Variables that should not be displayed
   */
  Set<String> getHiddenVariables();

  /**
   * @return All variables
   */
  Map<String, Object> getVariables();

  /**
   * @return {@link PrintStream} of standard output stream
   */
  PrintStream getStandardOut();
}
