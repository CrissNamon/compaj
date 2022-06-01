package com.hiddenproject.compaj.repl;

import java.util.*;
import com.hiddenproject.compaj.lang.*;
import com.hiddenproject.compaj.lang.groovy.*;
import com.hiddenproject.compaj.repl.utils.*;

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
