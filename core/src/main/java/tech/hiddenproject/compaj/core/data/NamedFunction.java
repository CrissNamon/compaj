package tech.hiddenproject.compaj.core.data;

import java.lang.reflect.Array;
import java.util.List;

import tech.hiddenproject.compaj.core.ReflectionUtil;
import tech.hiddenproject.compaj.core.model.DynamicFunction;

/**
 * Represents named function
 *
 * @param <N> Name type
 * @param <I> Input variables type
 * @param <O> Output variables type
 */
public interface NamedFunction<N, I, O> {

  /**
   * @return {@link DynamicFunction} binder of this function
   */
  DynamicFunction<I, O> getBinder();

  /**
   * Binds this function to {@link DynamicFunction}.
   *
   * @param formula {@link DynamicFunction}
   */
  void b(DynamicFunction<I, O> formula);

  /**
   * @return Name of this function
   */
  N getName();

  /**
   * Computes function for given values.
   *
   * @param data Input values
   * @return Output value
   */
  O value(I... data);

  /**
   * Computes function for given values.
   *
   * @param data Input values
   * @return Output value
   */
  O value(List<I> data);

  /**
   * Returns generic type as array type.
   *
   * @param c    {@link Class} to get generic from
   * @param name Full name of type to search
   * @param i    Generic position
   * @return Generic type
   */
  default Class getGenericAsArray(Class c, String name, Integer i) {
    Class generic = ReflectionUtil.getGeneric(c, name, i);
    I[] args = (I[]) Array.newInstance(generic, 0);
    return args.getClass();
  }

  /**
   * Binds this function to static value
   *
   * @param data Output value
   */
  void b(O data);
}
