package com.hiddenproject.compaj.core.data;

import com.hiddenproject.compaj.core.model.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public interface NamedFunction<N, I, O> {
  static <T> Object cast(Class<T> c, Object o) {
    return (T) o;
  }

  static <T> Object[] castToArray(Class<T> c, Object o) {
    return (T[]) o;
  }

  static Class getGeneric(Class toInspect, Class interf, Integer pos) {
    try {
      Type[] genericInterfaces = toInspect.getGenericInterfaces();
      for (Type genericInterface : genericInterfaces) {
        if (interf.getTypeName().equals(genericInterface.getTypeName())) {
          continue;
        }
        if (genericInterface instanceof ParameterizedType) {
          Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
          if (genericTypes[pos].getTypeName().endsWith("[]")) {
            Class s = Class.forName(genericTypes[pos].getTypeName().replace("[]", ""));
            return Array.newInstance(s, 0).getClass();
          }
          return Class.forName(genericTypes[pos].getTypeName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return Object.class;
  }

  static Class getGeneric(Class c, String name, Integer i) {
    try {
      Type[] genericInterfaces = c.getGenericInterfaces();
      for (Type genericInterface : genericInterfaces) {
        if (!genericInterface.getTypeName().contains(name)) {
          continue;
        }
        if (genericInterface instanceof ParameterizedType) {
          Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
          if (genericTypes[i].getTypeName().endsWith("[]")) {
            Class s = Class.forName(genericTypes[i].getTypeName().replace("[]", ""));
            return Array.newInstance(s, 0).getClass();
          }
          return Class.forName(genericTypes[i].getTypeName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return Object.class;
  }

  static <I, O> DynamicFunction<I, O> from(Supplier<O> s) {
    return x -> s.get();
  }

  DynamicFunction<I, O> getBinder();

  void b(DynamicFunction<I, O> formula);

  N getName();

  O value(I... data);

  O value(List<I> data);

  default Class getGenericAsArray(Class c, String name, Integer i) throws ClassNotFoundException {
    Class generic = getGeneric(c, name, i);
    I[] args = (I[]) Array.newInstance(generic, 0);
    return args.getClass();
  }

  void b(O data);

  interface A<T> {
    static <N> N[] create(Class<N> type, int length) {
      return (N[]) Array.newInstance(type, length);
    }

    default T[] createArray(int length) {
      Class<T> c = getGeneric(this.getClass(), A.class, 0);
      return create(c, length);
    }
  }
}
