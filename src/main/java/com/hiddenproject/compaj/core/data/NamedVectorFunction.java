package com.hiddenproject.compaj.core.data;

import java.util.function.Supplier;
import com.hiddenproject.compaj.core.model.DynamicFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.linear.RealVector;

public interface NamedVectorFunction<N, I, O> extends MultivariateVectorFunction {
  DynamicFunction<I, O[]> getBinder();
  void b(DynamicFunction<I, O[]> formula);
  N getName();
  O[] value(I... data);
  RealVector value(RealVector data);
  void b(I[] data);
  static <T extends Number> DynamicFunction<T, T[]> from(Supplier<T[]> s) {
    return x -> s.get();
  }
}
