package tech.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import tech.hiddenproject.compaj.core.data.NamedFunction;

/**
 * Generic math model.
 *
 * @param <F> Function name type
 * @param <I> Input variables type
 * @param <O> Output variables type
 */
public interface Model<F, I, O> {

  /**
   * @param label Function name
   * @return Function last result
   */
  O getAt(F label);

  /**
   * Adds new functions to model.
   *
   * @param e Array of {@link NamedFunction}
   */
  void a(NamedFunction<F, I, O>... e);

  /**
   * Adds data to function log.
   *
   * @param function Function to add values to
   * @param data     Log values
   */
  void ad(F function, O... data);

  /**
   * @return All functions in this model
   */
  Map<F, NamedFunction<F, I, O>> fns();

  /**
   * @return Log of all functions in model
   */
  Map<F, List<O>> fnslog();

  /**
   * @param label    Function name
   * @param position Position in log to get value from
   * @return Function result
   */
  O getAt(F label, int position);

  /**
   * Computes this model.
   *
   * @param data {@link Map} of initial values for functions
   */
  void compute(Map<F, I[]> data);

  /**
   * Computes this model.
   */
  void compute();

  /**
   * @return Model name
   */
  String getName();

  /**
   * Clears model log.
   */
  void clear();

  /**
   * Clears function log.
   *
   * @param f Function name
   */
  void clear(F f);

  /**
   * Binds update function to this model. Update function computes model values on each iteration.
   *
   * @param binder {@link Consumer}
   */
  void bu(Consumer<Map<F, I[]>> binder);
}
