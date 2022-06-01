package com.hiddenproject.compaj.lang.extension;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;
import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.model.*;
import groovy.lang.*;

public abstract class DynamicFunctionStaticExtension {
  public static DynamicFunction fromClosure(DynamicFunction self, Closure s) {
    return x -> s.call(x);
  }

  public static DynamicFunction fromClosureWithCast(DynamicFunction self, Closure s, Class outputType) {
    return x -> {
      Object result = s.call(x);
      if (outputType.isArray() && result instanceof ArrayList) {
        ArrayList r = (ArrayList) result;
        Object[] arr = (Object[]) Array.newInstance(outputType.getComponentType(), r.size());
        IntStream.range(0, arr.length)
            .forEachOrdered(i -> arr[i] = r.get(i));
        result = arr;
      }
      return NamedFunction.cast(outputType, result);
    };
  }

  public static DynamicFunction fromClosureWithCast(DynamicFunction self, Closure s, Class outputType, Closure f) {
    return x -> {
      Object result = s.call(x);
      if (outputType.isArray() && result instanceof ArrayList) {
        ArrayList r = (ArrayList) result;
        Object[] arr = (Object[]) Array.newInstance(outputType.getComponentType(), r.size());
        IntStream.range(0, arr.length)
            .forEachOrdered(i -> arr[i] = r.get(i));
        result = arr;
      }
      return f.call(result);
    };
  }
}
