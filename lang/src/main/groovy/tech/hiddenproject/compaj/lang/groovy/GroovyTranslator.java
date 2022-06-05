package tech.hiddenproject.compaj.lang.groovy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.extension.AgentExtension;
import tech.hiddenproject.compaj.lang.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.lang.extension.ComplexExtension;
import tech.hiddenproject.compaj.lang.extension.MathExtension;
import tech.hiddenproject.compaj.lang.extension.ModelExtension;
import tech.hiddenproject.compaj.lang.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.lang.extension.StarterExtension;

public class GroovyTranslator implements Translator {

  private static final List<CompilationCustomizer> customizerList = new ArrayList<>();
  private static final ImportCustomizer importCustomizer;

  private static final String[] normalImports =
      new String[] {
        "tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase",
        "tech.hiddenproject.compaj.lang.extension.MathExtension",
        "org.apache.commons.math3.analysis.MultivariateFunction",
        "java.lang.reflect.Array",
        "java.lang.reflect.ParameterizedType",
        "java.lang.reflect.Type",
        "tech.hiddenproject.compaj.core.model.DynamicFunction",
        "tech.hiddenproject.compaj.lang.extension.CompaJComplex"
      };

  private static final String[] starImports =
      new String[] {
        "tech.hiddenproject.compaj.core.data",
        "tech.hiddenproject.compaj.core.data.base",
        "tech.hiddenproject.compaj.core.model.base",
        "tech.hiddenproject.compaj.core.model",
        "tech.hiddenproject.compaj.applied.epidemic",
        "org.apache.commons.math3.ode.nonstiff",
        "org.apache.commons.math3.linear",
        "java.util.stream"
      };

  private static final String[] staticStarsImports = new String[] {"java.lang.Math"};

  static {
    importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(normalImports);
    importCustomizer.addStarImports(starImports);
    importCustomizer.addStaticStars(staticStarsImports);
    customizerList.add(importCustomizer);
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
    compilerConfiguration.addCompilationCustomizers(
        customizerList.toArray(new CompilationCustomizer[] {}));
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
    Object result = groovyShell.evaluate(translatorUtils.translate(script));
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
