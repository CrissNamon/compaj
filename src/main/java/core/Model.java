package core;

import java.util.Map;
import java.util.function.Supplier;
import annotation.ModelConstant;

public interface Model<C, S> {
  S time();
  Supplier<S> timeUpdater();
  void bindTime(Supplier<S> binder);
  void addCompartment(C compartmentLabel, S initial);
  void addConstant(C label, S initial);
  void bindCompartment(C compartment, Supplier<S> binder);
  Map<String, Compartment<S>> compartments();
  Map<String, Compartment<S>> constants();
  S compartment(C label);
  S constant(C label);
  void update();
  String getName();
}
