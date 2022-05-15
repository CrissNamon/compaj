package com.hiddenproject.compaj.core.utils;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

class CompajMathUtils {
  public static double[] arr(RealVector v) {
    return v.toArray();
  }

  public static double[][] arr(RealMatrix m) {
    return m.getData();
  }
}
