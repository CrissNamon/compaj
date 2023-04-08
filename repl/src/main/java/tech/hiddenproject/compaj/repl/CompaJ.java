package tech.hiddenproject.compaj.repl;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.math3.complex.Complex;
import tech.hiddenproject.compaj.extension.AgentExtension;
import tech.hiddenproject.compaj.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.extension.CompaJComplex;
import tech.hiddenproject.compaj.extension.ComplexExtension;
import tech.hiddenproject.compaj.extension.MathExtension;
import tech.hiddenproject.compaj.extension.ModelExtension;
import tech.hiddenproject.compaj.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.extension.StarterExtension;
import tech.hiddenproject.compaj.lang.FileUtils;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;

/**
 * Base class for REPL.
 */
public enum CompaJ {

  INSTANCE;

  static {
    init();
  }

  public static PrintStream out;

  private Translator translator;

  /**
   * Reads file content from url and evaluates it.
   *
   * @param url File url
   */
  public static void readFile(String url) {
    try {
      String script = FileUtils.readFromFile(new File(url));
      readInput(script);
    } catch (Throwable e) {
      out.println("Error: " + e.getLocalizedMessage());
    }
  }

  /**
   * Reads input source code and evaluates it.
   *
   * @param input Source code
   */
  public static void readInput(String input) {
    try {
      Object result = INSTANCE.getTranslator().evaluate(input);
      if (result != null) {
        if (result.getClass().isArray()) {
          out.println(Arrays.toString((Object[]) result));
        } else {
          out.println(result);
        }
      } else {
        out.print("");
        out.println();
      }
    } catch (Exception e) {
      out.println("ERROR: " + e.getClass());
      out.println(e.getMessage());
    }
  }

  /**
   * Exits from REPL.
   */
  public static void exit() {
    System.exit(0);
  }

  private static void init() {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
    Imports.normalImports.addAll(
        Set.of(
            CompaJ.class.getCanonicalName(),
            MathExtension.class.getCanonicalName(),
            CompaJComplex.class.getCanonicalName(),
            Complex.class.getCanonicalName()
        )
    );
  }

  public Translator getTranslator() {
    return translator;
  }

  public void setTranslator(Translator translator) {
    this.translator = translator;
    out = translator.getStandardOut();
  }
}
