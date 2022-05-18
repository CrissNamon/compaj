package com.hiddenproject.compaj.translator.extension.metaclass;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.ArrayRealVector;

public class ArrayRealVectorExtension {

  public static final String ADD_LIST_CONSTRUCTOR = "ArrayRealVector.metaClass.constructor << {\n" +
      "ArrayList<Double> d -> a = new double[d.size()] \n" +
      "for(int i = 0; i < d.size(); i++) a[i] = d.get(i) \n" +
      "new ArrayRealVector(a) }";

  public static final List<String> ArrayRealVectorExtension;

  static {
    ArrayRealVectorExtension = new ArrayList<>();
    ArrayRealVectorExtension.add(ADD_LIST_CONSTRUCTOR);
    ArrayRealVector d = new ArrayRealVector();

  }

}
