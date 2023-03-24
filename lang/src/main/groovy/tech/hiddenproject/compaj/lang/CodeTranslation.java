package tech.hiddenproject.compaj.lang;

/**
 * Represents translation block for {@link TranslatorUtils#translate(String)}.
 */
public interface CodeTranslation {

  /**
   * Translates source code to another lang.
   *
   * @param sourceCode Source code to translate
   * @return Translated source code
   */
  String translate(String sourceCode);
}
