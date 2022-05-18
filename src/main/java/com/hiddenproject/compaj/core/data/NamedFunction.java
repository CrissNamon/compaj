package com.hiddenproject.compaj.core.data;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import com.hiddenproject.compaj.core.model.DynamicFunction;

public interface NamedFunction<N, I, O> {
  DynamicFunction<I, O> getBinder();
  void b(DynamicFunction<I, O> formula);
  N getName();
  O value(I... data);

  void b(O data);

  default O call(I[] data) {
    try {
      Class<I> dataType = getGeneric(this.getClass(), NamedFunction.class.getName(), 1);
      I[] args = (I[]) Array.newInstance(dataType, data.length);
      IntStream.range(0, data.length).forEachOrdered(i -> args[i] = (I)NamedFunction.cast(dataType, data[i]));
      return value(args);
    }catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }

  static <T> Object cast(Class<T> c, Object o) {
    if(c.getSuperclass() == Number.class) {
      if(c == Double.class) {
        return ((Number)o).doubleValue();
      }
      if(c == Float.class){
        return ((Number)o).floatValue();
      }
    }
    return (T)o;
  }

  default Class<I> getGeneric(Class c, String name, int i) throws ClassNotFoundException {
    Type[] genericInterfaces = c.getGenericInterfaces();
    for (Type genericInterface : genericInterfaces) {
      if(!genericInterface.getTypeName().contains(name)) {
        continue;
      }
      if (genericInterface instanceof ParameterizedType) {
        Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
        return (Class<I>)Class.forName(genericTypes[i].getTypeName());
      }
    }
    return null;
  }

  static <I, O> DynamicFunction<I, O> from(Supplier<O> s) {
    return x -> s.get();
  }
}
