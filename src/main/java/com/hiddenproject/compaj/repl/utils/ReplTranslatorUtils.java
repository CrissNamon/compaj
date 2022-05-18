package com.hiddenproject.compaj.repl.utils;

import com.hiddenproject.compaj.translator.base.GroovyTranslatorUtils;

public class ReplTranslatorUtils extends GroovyTranslatorUtils {

  public ReplTranslatorUtils() {
    super.addCodeTranslation(this::eraseTypes);
  }

  @Override
  public String translate(String script) {
    return super.translate(script);
  }

  private String eraseTypes(String script) {
    return script.replaceAll("[ ]*(\\w++)[ ]*(\\w++)[ ]*=", "$2 =");
  }
}
