package com.hiddenproject.compaj.translator.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MathExtension {
  public static List<Double> sin(Double... data) {
    return Arrays.stream(data).map(d -> Math.sin(d.doubleValue())).collect(Collectors.toList());
  }

  public static List<Double> sin(ArrayList<Double> data) {
    return data.stream().map(d -> Math.sin(d.doubleValue())).collect(Collectors.toList());
  }
}
