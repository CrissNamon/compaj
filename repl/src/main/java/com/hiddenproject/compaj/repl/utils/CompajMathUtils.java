package com.hiddenproject.compaj.repl.utils;

import org.apache.commons.math3.linear.*;

class CompajMathUtils {
  public static double[] arr(RealVector v) {
    return v.toArray();
  }

  public static double[][] arr(RealMatrix m) {
    return m.getData();
  }
}
