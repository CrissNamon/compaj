package tech.hiddenproject.compaj.repl.utils;

import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;

public class ReplTranslatorUtils extends GroovyTranslatorUtils {

  public ReplTranslatorUtils() {
    super.addCodeTranslation(this::eraseTypes);
  }

  private String eraseTypes(String script) {
    return script.replaceAll("[ ]*(\\w++)[ ]*(\\w++)[ ]*=", "$2 =");
  }

  @Override
  public String translate(String script) {
    return super.translate(script);
  }
}
