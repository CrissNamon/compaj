package com.hiddenproject.compaj.repl;

import com.hiddenproject.compaj.core.translator.base.GroovyTranslatorUtils;

public class ReplTranslatorUtils extends GroovyTranslatorUtils {

  @Override
  public String translate(String script) {
    return eraseTypes(super.translate(script));
  }

  private String eraseTypes(String script) {
    return script.replaceAll("[ ]*(\\w++)[ ]*(\\w++)[ ]*=", "$2 =");
  }
}
