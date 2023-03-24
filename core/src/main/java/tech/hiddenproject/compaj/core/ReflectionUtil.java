package tech.hiddenproject.compaj.core;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {

  /**
   * Returns generic type.
   *
   * @param c    {@link Class} to get generic from
   * @param name Full name of type to search
   * @param i    Generic position
   * @return Generic type
   */
  public static Class getGeneric(Class c, String name, Integer i) {
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
}
