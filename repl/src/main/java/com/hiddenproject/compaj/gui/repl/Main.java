package com.hiddenproject.compaj.gui.repl;

import java.util.Arrays;
import java.util.Scanner;
import com.hiddenproject.compaj.gui.repl.utils.ReplTranslatorUtils;
import com.hiddenproject.compaj.gui.translator.Translator;
import com.hiddenproject.compaj.gui.translator.TranslatorUtils;
import com.hiddenproject.compaj.gui.translator.groovy.GroovyTranslator;

public class Main {

  public static void main(String[] args) {
    TranslatorUtils translatorUtils = new ReplTranslatorUtils();
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
