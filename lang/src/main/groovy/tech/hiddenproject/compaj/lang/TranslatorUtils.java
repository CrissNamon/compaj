package tech.hiddenproject.compaj.lang;

import java.util.Set;

public interface TranslatorUtils {
  String translate(String script);
  String translate(String script, Set<CodeTranslation> codeTranslations);

  void useRawLanguage(boolean f);

  void addCodeTranslation(CodeTranslation translation);

  boolean isLexemeInString(int start, int end);
}
