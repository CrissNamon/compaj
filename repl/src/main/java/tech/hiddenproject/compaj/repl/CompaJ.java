package tech.hiddenproject.compaj.repl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tech.hiddenproject.compaj.extension.AgentExtension;
import tech.hiddenproject.compaj.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.extension.ComplexExtension;
import tech.hiddenproject.compaj.extension.MathExtension;
import tech.hiddenproject.compaj.extension.ModelExtension;
import tech.hiddenproject.compaj.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.extension.StarterExtension;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;

public class CompaJ {

  private static final String[] normalImports =
      new String[] {
          "tech.hiddenproject.compaj.extension.MathExtension",
          "tech.hiddenproject.compaj.extension.CompaJComplex"
      };

  private static CompaJ INSTANCE;

  private Translator translator;

  {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  {
    GroovyTranslator.getImportCustomizer()
        .addImports(normalImports);
  }

  private CompaJ() {
  }

  public static void readFile(String url) {
    try {
      Path path = Paths.get(url);
      String script = new String(Files.readAllBytes(path));
      getInstance().getTranslator().evaluate(script);
    } catch (IOException e) {
      System.out.println("Error: " + e.getLocalizedMessage());
    }
  }

  public static void readInput(String input) {
    try {
      Object result = getInstance().getTranslator().evaluate(input);
      if (result != null) {
        if (result.getClass().isArray()) {
          System.out.println(Arrays.toString((Object[]) result));
        } else {
          System.out.println(result);
        }
      } else {
        System.out.print("");
        System.out.println();
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  public Translator getTranslator() {
    return translator;
  }

  public static CompaJ getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CompaJ();
    }
    return INSTANCE;
  }

  public void setTranslator(Translator translator) {
    this.translator = translator;
  }

  public static void exit() {
    System.exit(0);
  }

  public static Map<String, List<String>> getCLIParams(String[] args) {
    final Map<String, List<String>> params = new HashMap<>();
    List<String> options = null;
    for (final String a : args) {
      if (a.charAt(0) == '-') {
        if (a.length() < 2) {
          continue;
        }
        options = new ArrayList<>();
        params.put(a.substring(1), options);
      } else if (options != null) {
        options.add(a);
      }
    }
    return params;
  }
}
