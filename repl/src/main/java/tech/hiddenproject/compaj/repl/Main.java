package tech.hiddenproject.compaj.repl;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;

public class Main {

  private static Translator translator;

  public static void main(String[] args) {
    TranslatorUtils translatorUtils =
        new GroovyTranslatorUtils();
    GroovyTranslator.getImportCustomizer()
        .addImports("tech.hiddenproject.compaj.repl.CompaJ")
        .addStaticImport("tech.hiddenproject.compaj.repl.CompaJ", "exit");
    translator = new GroovyTranslator(translatorUtils);
    CompaJ.getInstance().setTranslator(translator);
    processArgs(args);
    System.out.println("Welcome to CompaJ REPL!");
    System.out.println("Version 0.0.3");
    processInput();
  }

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

  private static void processInput() {
    Scanner sc = new Scanner(System.in);
    do {
      System.out.print("> ");
      String input = sc.nextLine();
      CompaJ.readInput(input);
    } while (true);
  }

}
