package tech.hiddenproject.compaj.repl;

import java.util.Arrays;
import java.util.Scanner;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.repl.utils.ReplTranslatorUtils;

public class Main {

  public static void main(String[] args) {
    TranslatorUtils translatorUtils =
        new ReplTranslatorUtils();
    GroovyTranslator.getImportCustomizer()
        .addImports("tech.hiddenproject.compaj.repl.Compaj")
        .addStaticImport("tech.hiddenproject.compaj.repl.Compaj", "exit");
    Translator translator = new GroovyTranslator(translatorUtils);
    CompaJ.getInstance().setTranslator(translator);
    Scanner sc = new Scanner(System.in);
    do {
      System.out.print("> ");
      String input = sc.nextLine();
      try {
        Object result = translator.evaluate(input);
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
    } while (true);
  }
}
