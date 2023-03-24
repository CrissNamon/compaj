package tech.hiddenproject.compaj.lang;

import java.util.Set;

/**
 * Represents utils for {@link Translator}.
 */
public interface TranslatorUtils {

  /**
   * Translates script from one lang to another.
   *
   * @param script Script to translate
   * @return Translated script
   */
  String translate(String script);

  /**
   * Translates script from one lang to another.
   *
   * @param script           Script to translate
   * @param codeTranslations Set of {@link CodeTranslation} to use in translation process
   * @return Translated script
   */
  String translate(String script, Set<CodeTranslation> codeTranslations);

  /**
   * Checks if given string block is a string.
   *
   * @param start Start position of block
   * @param end   End position of block
   * @return true if given block is in string
   */
  boolean isLexemeInString(int start, int end);
}
