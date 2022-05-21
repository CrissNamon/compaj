package com.hiddenproject.compaj.repl;

import com.hiddenproject.compaj.repl.utils.*;
import com.hiddenproject.compaj.translator.*;
import com.hiddenproject.compaj.translator.groovy.*;

import java.util.*;

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
