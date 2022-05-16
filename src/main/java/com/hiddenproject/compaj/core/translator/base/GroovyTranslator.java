package com.hiddenproject.compaj.core.translator.base;

import java.util.*;
import java.util.stream.Collectors;
import com.hiddenproject.compaj.core.data.Equation;
import com.hiddenproject.compaj.core.data.Variable;
import com.hiddenproject.compaj.core.translator.Translator;
import com.hiddenproject.compaj.core.translator.TranslatorUtils;
import groovy.lang.*;
import org.apache.groovy.ast.tools.ClassNodeUtils;
import org.apache.groovy.metaclass.MetaClass;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

public class GroovyTranslator implements Translator {

  private final static List<CompilationCustomizer> customizerList = new ArrayList<>();

  private final static String[] normalImports = new String[]{
      "com.hiddenproject.compaj.core.CompaJ"
  };

  private final static String[] starImports = new String[] {
      "com.hiddenproject.compaj.core.data",
      "com.hiddenproject.compaj.core.data.base",
      "com.hiddenproject.compaj.core.model.base",
      "com.hiddenproject.compaj.core.model",
      "com.hiddenproject.compaj.core.model.epidemic",
      "org.apache.commons.math3.ode.nonstiff",
      "org.apache.commons.math3.linear"
  };

  private final static String[] staticStarsImports = new String[] {
      "java.lang.Math",
      "com.hiddenproject.compaj.core.utils.CompajMathUtils"
  };

  static {
    ImportCustomizer importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(normalImports);
    importCustomizer.addStarImports(starImports);
    importCustomizer.addStaticStars(staticStarsImports);
    importCustomizer.addStaticImport("com.hiddenproject.compaj.core.CompaJ", "exit");
    customizerList.add(importCustomizer);
  }

  private final GroovyShell groovyShell;
  private final Binding binding;
  private final TranslatorUtils translatorUtils;

  public GroovyTranslator(TranslatorUtils translatorUtils, CompilationCustomizer... compilationCustomizer) {
    customizerList.addAll(Arrays.stream(compilationCustomizer).collect(Collectors.toList()));
    CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.addCompilationCustomizers(customizerList.toArray(new CompilationCustomizer[]{}));
    this.binding = new Binding();
    this.groovyShell = new GroovyShell(binding, compilerConfiguration);
    this.translatorUtils = translatorUtils;
    GroovySystem.getMetaClassRegistry().setMetaClassCreationHandle(new MetaClassRegistry.MetaClassCreationHandle());
  }

  public Object evaluate(String script) {
    return groovyShell.evaluate(
        translatorUtils.translate(script)
    );
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
