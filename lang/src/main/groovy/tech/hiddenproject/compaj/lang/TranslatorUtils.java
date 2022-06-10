package tech.hiddenproject.compaj.lang;

public interface TranslatorUtils {
  String translate(String script);

  void useRawLanguage(boolean f);

  void addCodeTranslation(CodeTranslation translation);

  boolean isLexemeInString(int start, int end);
}
