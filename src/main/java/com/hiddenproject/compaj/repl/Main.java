package com.hiddenproject.compaj.repl;

import java.io.IOException;
import java.util.Scanner;
import com.hiddenproject.compaj.core.CompaJ;
import com.hiddenproject.compaj.core.translator.Translator;
import com.hiddenproject.compaj.core.translator.TranslatorUtils;
import com.hiddenproject.compaj.core.translator.base.GroovyTranslator;
import com.hiddenproject.compaj.framework.annotation.Simulation;

@Simulation(epidemic = "Test")
public class Main {

  public static void main(String[] args) throws IOException {
    TranslatorUtils translatorUtils = new ReplTranslatorUtils();
    Translator translator = new GroovyTranslator(translatorUtils);
    CompaJ.getInstance().setTranslator(translator);
    CompaJ.readFile("");
    Scanner sc = new Scanner(System.in);
    do{
      System.out.print("> ");
      String input = sc.nextLine();
      try {
        Object result = translator.evaluate(input);
        if(result != null) {
          System.out.println(result);
        } else {
          System.out.print("");
          System.out.println();
        }
      }catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }while(true);
  }
}
