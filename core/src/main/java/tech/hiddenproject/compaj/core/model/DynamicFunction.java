package tech.hiddenproject.compaj.core.model;

import java.util.function.Supplier;

/**
 * Represents dynamic function with var args.
 *
 * @param <T> Input args type
 * @param <R> Output type
 */
@FunctionalInterface
public interface DynamicFunction<T, R> {

  static <I, O> DynamicFunction<I, O> from(Supplier<O> s) {
    return x -> s.get();
  }

  R apply(T... args);
}
