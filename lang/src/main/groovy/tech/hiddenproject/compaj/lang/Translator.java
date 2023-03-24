package tech.hiddenproject.compaj.lang;

import java.util.Set;

import org.codehaus.groovy.control.CompilerConfiguration;

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

  CompilerConfiguration getConfig();
}
