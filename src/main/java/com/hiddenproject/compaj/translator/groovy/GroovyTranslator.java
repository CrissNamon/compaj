package com.hiddenproject.compaj.translator.groovy;

import java.util.*;
import java.util.stream.Collectors;
import com.hiddenproject.compaj.CompaJScriptBase;
import com.hiddenproject.compaj.translator.Translator;
import com.hiddenproject.compaj.translator.TranslatorUtils;
import com.hiddenproject.compaj.translator.extension.*;
import groovy.lang.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

public class GroovyTranslator implements Translator {

  private final static List<CompilationCustomizer> customizerList = new ArrayList<>();

  private final static String[] normalImports = new String[]{
      "com.hiddenproject.compaj.CompaJScriptBase",
      "com.hiddenproject.compaj.repl.CompaJ",
      "com.hiddenproject.compaj.translator.extension.MathExtension",
      "org.apache.commons.math3.analysis.MultivariateFunction",
      "java.lang.reflect.Array",
      "java.lang.reflect.ParameterizedType",
      "java.lang.reflect.Type",
      "com.hiddenproject.compaj.core.model.DynamicFunction"
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

  static {
    CompaJScriptBase.addExtension(new StarterExtension());
  }

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;
  private final Map<String, Object> variables;

  {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
  }

  public GroovyTranslator(TranslatorUtils translatorUtils, CompilationCustomizer... compilationCustomizer) {
    customizerList.addAll(Arrays.stream(compilationCustomizer).collect(Collectors.toList()));
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.addCompilationCustomizers(customizerList.toArray(new CompilationCustomizer[]{}));
    compilerConfiguration.setScriptBaseClass("CompaJScriptBase");
    this.binding = new Binding();
    this.variables = binding.getVariables();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
  }

  public Object evaluate(String script) {
    Object result = groovyShell.evaluate(
        translatorUtils.translate(script)
    );
    getBinding().setVariable("trn", result);
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
