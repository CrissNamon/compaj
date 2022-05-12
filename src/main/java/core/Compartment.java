package core;

import java.util.function.Supplier;

public interface Compartment<D> {
  D getData();
  Supplier<D> getFormula();
  void setData(Double data);
  void setFormula(Supplier<Double> formula);
}
