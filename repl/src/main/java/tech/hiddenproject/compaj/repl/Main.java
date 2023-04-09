package tech.hiddenproject.compaj.repl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;
import tech.hiddenproject.compaj.repl.exception.ArgumentValueException;

public class Main {

  private static final List<String> LIBRARIES = new ArrayList<>();
  private static final Map<String, List<String>> ARGUMENTS = new HashMap<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(CompaJ.class);

  // No logs, because they will spam REPL console
  static {
    org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
  }

  public static void main(String... args) {
    processArgs(args);
    processLibrariesArg();
    init();
    processFileInputArg();
    processInputStringArg();
    CompaJ.out.println("Welcome to CompaJ REPL!");
    CompaJ.out.println("Version 0.0.4");
    processInput();
  }

  /**
   * Initializes {@link GroovyTranslator}.
   */
  protected static void init() {
    TranslatorUtils translatorUtils =
        new GroovyTranslatorUtils();
    Translator translator = new GroovyTranslator(translatorUtils, LIBRARIES);
    CompaJ.INSTANCE.setTranslator(translator);
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
    Map<String, List<String>> mappedData = getCLIParams(args);
    ARGUMENTS.putAll(mappedData);
  }

  /**
   * Maps CLI parameters to {@link Map}.
   *
   * @param args CLI arguments
   * @return {@link Map} of arguments
   */
  private static Map<String, List<String>> getCLIParams(String[] args) {
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

  private static void processLibrariesArg() {
    if (ARGUMENTS.containsKey("l")) {
      List<String> libsPaths = ARGUMENTS.get("l");
      LOGGER.info("Loading libs: {}", libsPaths);
      LIBRARIES.addAll(libsPaths);
    }
  }

  private static void processInputStringArg() {
    if (ARGUMENTS.containsKey("s")) {
      List<String> sources = ARGUMENTS.get("s");
      if (sources.isEmpty()) {
        throw new ArgumentValueException("Flag -s found, but no scripts provided!");
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
      if (files.isEmpty()) {
        throw new ArgumentValueException("Flag -f found, but no files provided!");
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
      CompaJ.out.print("> ");
      String input = sc.nextLine();
      CompaJ.readInput(input);
    } while (true);
  }

}
