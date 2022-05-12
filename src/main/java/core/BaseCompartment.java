package core;

import java.util.function.Supplier;

public class BaseCompartment implements Compartment<Double> {

  private Double data;
  private Supplier<Double> formula;

  public Double getData() {
    return data;
  }

  public void setData(Double data) {
    this.data = data;
  }

  public Supplier<Double> getFormula() {
    return formula;
  }

  public void setFormula(Supplier<Double> formula) {
    this.formula = formula;
  }
}
