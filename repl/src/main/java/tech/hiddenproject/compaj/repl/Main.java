package tech.hiddenproject.compaj.repl;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;

public class Main {

  public static void main(String... args) {
    init();
    processArgs(args);
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
    Translator translator = new GroovyTranslator(translatorUtils);
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
    if (mappedData.containsKey("f")) {
      List<String> files = mappedData.get("f");
      if (files.size() == 0) {
        throw new RuntimeException("Flag -f found, but no files provided!");
      }
      for (String file : files) {
        CompaJ.readFile(file);
      }
      CompaJ.exit();
    }
    if (mappedData.containsKey("s")) {
      List<String> sources = mappedData.get("s");
      if (sources.size() == 0) {
        throw new RuntimeException("Flag -s found, but no scripts provided!");
      }
      for (String source : sources) {
        CompaJ.readInput(source);
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
