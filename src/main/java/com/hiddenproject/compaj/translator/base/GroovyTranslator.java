package com.hiddenproject.compaj.translator.base;

import java.util.*;
import java.util.stream.Collectors;
import com.hiddenproject.compaj.translator.Translator;
import com.hiddenproject.compaj.translator.TranslatorUtils;
import groovy.lang.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class GroovyTranslator implements Translator {

  private final static List<CompilationCustomizer> customizerList = new ArrayList<>();

  private final static String[] normalImports = new String[]{
      "com.hiddenproject.compaj.repl.CompaJ",
      "com.hiddenproject.compaj.translator.extension.MathExtension"
  };

  private final static String[] starImports = new String[] {
      "com.hiddenproject.compaj.core.data",
      "com.hiddenproject.compaj.core.data.base",
      "com.hiddenproject.compaj.core.model.base",
      "com.hiddenproject.compaj.core.model",
      "com.hiddenproject.compaj.applied.epidemic",
      "org.apache.commons.math3.ode.nonstiff",
      "org.apache.commons.math3.linear",
      "java.util.stream"
  };

  private final static String[] staticStarsImports = new String[] {
      "com.hiddenproject.compaj.repl.utils.CompajMathUtils",
      "com.hiddenproject.compaj.translator.extension.MathExtension",
      "java.lang.Math"
  };

  static {
    ImportCustomizer importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(normalImports);
    importCustomizer.addStarImports(starImports);
    importCustomizer.addStaticStars(staticStarsImports);
    importCustomizer.addStaticImport("com.hiddenproject.compaj.repl.CompaJ", "exit");
    customizerList.add(importCustomizer);
  }

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;
  private final Map<String, Object> variables;

  public GroovyTranslator(TranslatorUtils translatorUtils, CompilationCustomizer... compilationCustomizer) {
    customizerList.addAll(Arrays.stream(compilationCustomizer).collect(Collectors.toList()));
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.addCompilationCustomizers(customizerList.toArray(new CompilationCustomizer[]{}));
    this.binding = new Binding();
    this.variables = binding.getVariables();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
    //GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(new MetaClassRegistry.MetaClassCreationHandle());
  }

  public Object evaluate(String script) {
    Object result = groovyShell.evaluate(
        translatorUtils.translate(script)
    );
    //updateVariables();
    return result;
  }

  private void updateVariables() {
    Map<String, Object> tmp = getBinding().getVariables();
    for(Map.Entry<String, Object> entry : tmp.entrySet()) {
      if(variables.containsKey(entry.getKey()) && !variables.get(entry.getKey()).equals(entry.getValue())) {
        //System.out.println("VARIABLE UPDATED: " + entry.getKey() + " WITH VALUE: " + entry.getValue());
      }
      variables.put(entry.getKey(), entry.getValue());
    }
  }

  public Class[] getLoadedClasses() {
    return groovyShell.getClassLoader().getLoadedClasses();
  }

  public Binding getBinding() {
    return binding;
  }

  public GroovyShell getGroovyShell() {
    return groovyShell;
  }

}
