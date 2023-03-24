package tech.hiddenproject.compaj.repl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;

public class Main {

  private static final List<String> LIBRARIES = new ArrayList<>();

  private static final Map<String, List<String>> ARGUMENTS = new HashMap<>();

  public static void main(String... args) {
    processArgs(args);
    processLibrariesArg();
    init();
    processFileInputArg();
    processInputStringArg();
    System.out.println("Welcome to CompaJ REPL!");
    System.out.println("Version 0.0.3");
    processInput();
  }

  /**
   * Initializes {@link GroovyTranslator}.
   */
  protected static void init() {
    TranslatorUtils translatorUtils =
        new GroovyTranslatorUtils();
    GroovyTranslator.getImportCustomizer()
        .addImports("tech.hiddenproject.compaj.repl.CompaJ")
        .addStaticImport("tech.hiddenproject.compaj.repl.CompaJ", "exit");
    Translator translator = new GroovyTranslator(translatorUtils, LIBRARIES);
    CompaJ.getInstance().setTranslator(translator);
  }

  /**
   * Processes REPL arguments.
   * <br>
   * Supported arguments:
   * <ul>
   *   <li>-f [fileUrl] Read from file</li>
   *   <li>-s [sourceCode] Evaluate string</li>
   * </ul>
   *
   * @param args REPL arguments
   */
  private static void processArgs(String... args) {
    Map<String, List<String>> mappedData = CompaJ.getCLIParams(args);
    ARGUMENTS.putAll(mappedData);
  }

  private static void processLibrariesArg() {
    if (ARGUMENTS.containsKey("l")) {
      List<String> libsPaths = ARGUMENTS.get("l");
      System.out.println("Loading libs: " + libsPaths);
      LIBRARIES.addAll(libsPaths);
    }
  }

  private static void processInputStringArg() {
    if (ARGUMENTS.containsKey("s")) {
      List<String> sources = ARGUMENTS.get("s");
      if (sources.size() == 0) {
        throw new RuntimeException("Flag -s found, but no scripts provided!");
      }
      for (String source : sources) {
        CompaJ.readInput(source);
      }
      CompaJ.exit();
    }
  }

  private static void processFileInputArg() {
    if (ARGUMENTS.containsKey("f")) {
      List<String> files = ARGUMENTS.get("f");
      if (files.size() == 0) {
        throw new RuntimeException("Flag -f found, but no files provided!");
      }
      for (String file : files) {
        CompaJ.readFile(file);
      }
      CompaJ.exit();
    }
  }

  /**
   * Processes user input.
   */
  private static void processInput() {
    Scanner sc = new Scanner(System.in);
    do {
      System.out.print("> ");
      String input = sc.nextLine();
      CompaJ.readInput(input);
    } while (true);
  }

}
