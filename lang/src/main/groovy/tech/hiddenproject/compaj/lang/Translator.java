package tech.hiddenproject.compaj.lang;

import java.util.Set;

public interface Translator {

  Object evaluate(String script);
  Object evaluate(String script, Set<CodeTranslation> codeTranslations);
  TranslatorUtils getTranslatorUtils();
}
