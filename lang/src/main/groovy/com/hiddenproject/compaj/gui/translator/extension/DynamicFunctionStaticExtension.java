package com.hiddenproject.compaj.gui.translator.extension;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.IntStream;
import groovy.lang.Closure;

public abstract class DynamicFunctionStaticExtension {
  public static com.hiddenproject.compaj.gui.core.model.DynamicFunction fromClosure(com.hiddenproject.compaj.gui.core.model.DynamicFunction self, Closure s) {
    return x -> s.call(x);
  }

  public static com.hiddenproject.compaj.gui.core.model.DynamicFunction fromClosureWithCast(com.hiddenproject.compaj.gui.core.model.DynamicFunction self, Closure s, Class outputType) {
    return x -> {
      Object result = s.call(x);
      if (outputType.isArray() && result instanceof ArrayList) {
        ArrayList r = (ArrayList) result;
        Object[] arr = (Object[]) Array.newInstance(outputType.getComponentType(), r.size());
        IntStream.range(0, arr.length)
            .forEachOrdered(i -> arr[i] = r.get(i));
        result = arr;
      }
      return com.hiddenproject.compaj.gui.core.data.NamedFunction.cast(outputType, result);
    };
  }

  public static com.hiddenproject.compaj.gui.core.model.DynamicFunction fromClosureWithCast(com.hiddenproject.compaj.gui.core.model.DynamicFunction self, Closure s, Class outputType, Closure f) {
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
