package com.hiddenproject.compaj.core.translator.base;

import com.hiddenproject.compaj.core.translator.Translator;
import com.hiddenproject.compaj.core.translator.TranslatorUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;

public class GroovyTranslator implements Translator {

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;

  public GroovyTranslator(TranslatorUtils translatorUtils) {
    binding = new Binding();
    groovyShell = new GroovyShell(binding);
    this.translatorUtils = translatorUtils;
    translatorUtils.startUp(this);
  }

  public Object evaluate(String script) {
    return groovyShell.evaluate(
        translatorUtils.translate(script)
    );
  }

  public Class[] getLoadedClasses() {
    return groovyShell.getClassLoader().getLoadedClasses();
  }
}
