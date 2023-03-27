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
public class CompaJ {
  private static CompaJ INSTANCE;

  static {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  static {
    Imports.normalImports.addAll(
        Set.of(
            CompaJ.class.getCanonicalName(),
            MathExtension.class.getCanonicalName(),
            CompaJComplex.class.getCanonicalName(),
            Complex.class.getCanonicalName()
        )
    );
  }

  private Translator translator;
  public PrintStream out;

  private CompaJ() {}

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
      getInstance().out.println("Error: " + e.getLocalizedMessage());
    }
  }

  /**
   * Reads input source code and evaluates it.
   *
   * @param input Source code
   */
  public static void readInput(String input) {
    try {
      Object result = getInstance().getTranslator().evaluate(input);
      if (result != null) {
        if (result.getClass().isArray()) {
          getInstance().out.println(Arrays.toString((Object[]) result));
        } else {
          getInstance().out.println(result);
        }
      } else {
        getInstance().out.print("");
        getInstance().out.println();
      }
    } catch (Exception e) {
      getInstance().out.println("ERROR: " + e.getClass());
      getInstance().out.println(e.getMessage());
    }
  }

  public static CompaJ getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CompaJ();
    }
    return INSTANCE;
  }

  /**
   * Exits from REPL.
   */
  public static void exit() {
    System.exit(0);
  }

  public Translator getTranslator() {
    return translator;
  }

  public void setTranslator(Translator translator) {
    this.translator = translator;
    this.out = translator.getStandardOut();
  }
}
