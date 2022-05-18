package com.hiddenproject.compaj.translator.extension;

import java.util.Arrays;
import com.hiddenproject.compaj.core.data.base.RealFunction;
import groovy.lang.Closure;

public class RealFunctionExtension {
  public static void b(RealFunction self, Closure s){
    int paramsCount = s.getMaximumNumberOfParameters();
    Double[] params = new Double[paramsCount];
    Arrays.fill(params, 0.0);
    try{
      Number n = (Number)s.call(params);
      self.b((x) -> ((Number)s.call(x)).doubleValue());
    }catch (ClassCastException e) {
      System.out.println(e.getMessage());
    }
  }
}
