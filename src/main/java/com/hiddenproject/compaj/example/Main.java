package com.hiddenproject.compaj.example;

import java.util.Scanner;
import com.hiddenproject.compaj.core.CompaJ;
import com.hiddenproject.compaj.core.translator.Translator;
import com.hiddenproject.compaj.framework.annotation.Simulation;

@Simulation(epidemic = "Test")
public class Main {

  public static void main(String[] args) {

    Translator translator = CompaJ.getInstance().getTranslator();
    Scanner sc = new Scanner(System.in);

    while(true){
      System.out.print("> ");
      String input = sc.nextLine();
      try {
        Object result = translator.evaluate(input);
        if(result != null) {
          System.out.println(result);
        }
      }catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
