package com.hiddenproject.compaj.lang.groovy;

import java.io.*;
import java.util.*;
import com.hiddenproject.compaj.lang.*;
import com.hiddenproject.compaj.lang.extension.*;
import groovy.lang.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.customizers.*;

public class GroovyTranslator implements Translator {

  private static final List<CompilationCustomizer> customizerList = new ArrayList<>();
  private static final ImportCustomizer importCustomizer;

  private static final String[] normalImports = new String[]{
      "com.hiddenproject.compaj.lang.groovy.CompaJScriptBase",
      //"com.hiddenproject.compaj.repl.CompaJ",
      "com.hiddenproject.compaj.lang.extension.MathExtension",
      "org.apache.commons.math3.analysis.MultivariateFunction",
      "java.lang.reflect.Array",
      "java.lang.reflect.ParameterizedType",
      "java.lang.reflect.Type",
      "com.hiddenproject.compaj.core.model.DynamicFunction",
      "com.hiddenproject.compaj.lang.extension.CompaJComplex"
  };

  private static final String[] starImports = new String[]{
      "com.hiddenproject.compaj.core.data",
      "com.hiddenproject.compaj.core.data.base",
      "com.hiddenproject.compaj.core.model.base",
      "com.hiddenproject.compaj.core.model",
      "com.hiddenproject.compaj.applied.epidemic",
      "org.apache.commons.math3.ode.nonstiff",
      "org.apache.commons.math3.linear",
      "java.util.stream"
  };

  private static final String[] staticStarsImports = new String[]{
      "java.lang.Math"
  };

  static {
    importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(normalImports);
    importCustomizer.addStarImports(starImports);
    importCustomizer.addStaticStars(staticStarsImports);
    //importCustomizer.addStaticImport("com.hiddenproject.compaj.repl.CompaJ", "exit");
    customizerList.add(importCustomizer);
  }

  static {
    CompaJScriptBase.addExtension(new StarterExtension());
  }

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;
  private final Map<String, Object> variables;
  private final ByteArrayOutputStream output;

  {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  public GroovyTranslator(TranslatorUtils translatorUtils) {
    this("CompaJScriptBase", translatorUtils);
  }

  public GroovyTranslator(String base, TranslatorUtils translatorUtils) {
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.addCompilationCustomizers(customizerList.toArray(new CompilationCustomizer[]{}));
    compilerConfiguration.setScriptBaseClass(base);
    this.binding = new Binding();
    output = new ByteArrayOutputStream();
    binding.setVariable("out", new PrintStream(output));
    this.variables = binding.getVariables();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
  }

  public static ImportCustomizer getImportCustomizer() {
    return importCustomizer;
  }

  public Object evaluate(String script) {
    output.reset();
    Object result = groovyShell.evaluate(
        translatorUtils.translate(script)
    );
    updateVariables();
    if (result != null) {
      getBinding().setVariable("trn", result);
    }
    if (output.toString().isEmpty()) {
      return result;
    }
    return output.toString();
  }

  private void updateVariables() {
    Map<String, Object> tmp = getBinding().getVariables();
    for (Map.Entry<String, Object> entry : tmp.entrySet()) {
      variables.put(entry.getKey(), entry.getValue());
    }
  }

  private Binding getBinding() {
    return binding;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  private Class[] getLoadedClasses() {
    return groovyShell.getClassLoader().getLoadedClasses();
  }

  private GroovyShell getGroovyShell() {
    return groovyShell;
  }

}
