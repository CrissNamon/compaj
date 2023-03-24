package tech.hiddenproject.compaj.applied.epidemic;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import tech.hiddenproject.compaj.core.data.base.RealFunction;
import tech.hiddenproject.compaj.core.model.DynamicFunction;
import tech.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;

/**
 * Epidemic SIR model.
 */
public class SIRModel {

  private final FirstOrderDifferentialModel model;
  private final RealFunction S;
  private final RealFunction I;
  private final RealFunction R;
  private final Double sInit;
  private final Double iInit;
  private final Double rInit;
  private Double a = 0.04;
  private Double b = 0.4;

  public SIRModel(Double sInit, Double iInit, Double rInit) {
    this("SISModel", sInit, iInit, rInit);
  }

  public SIRModel(String name, Double sInit, Double iInit, Double rInit) {
    this.sInit = sInit;
    this.iInit = iInit;
    this.rInit = rInit;
    model = new FirstOrderDifferentialModel(name);
    model.i(new EulerIntegrator(1));
    S = new RealFunction("S");
    I = new RealFunction("I");
    R = new RealFunction("R");
    model.a(S, I, R);
    model.ad("S", sInit);
    model.ad("I", iInit);
    model.ad("R", rInit);
    S.b(DynamicFunction.from(this::susceptible));
    I.b(DynamicFunction.from(this::infected));
    R.b(DynamicFunction.from(this::recovered));
  }

  private Double susceptible() {
    return -b * model.getAt("S") * model.getAt("I") / total();
  }

  private Double infected() {
    return b * model.getAt("S") * model.getAt("I") / total() - a * model.getAt("I");
  }

  private Double recovered() {
    return a * model.getAt("I");
  }

  private Double total() {
    return (model.getAt("S") + model.getAt("I") + model.getAt("R"));
  }

  public SIRModel withAlpha(Double value) {
    a = value;
    return this;
  }

  public SIRModel withBetta(Double value) {
    b = value;
    return this;
  }

  public void compute(Double lowBound, Double highBound) {
    clear();
    model.ad("S", sInit);
    model.ad("I", iInit);
    model.ad("R", rInit);
    model.compute(lowBound, highBound);
  }

  public void clear() {
    model.clear();
  }

  public void i(FirstOrderIntegrator firstOrderIntegrator) {
    model.i(firstOrderIntegrator);
  }

  @Override
  public String toString() {
    return model.toString();
  }

  public Double getAt(String c) {
    return model.getAt(c);
  }

  public Double getAt(String c, int p) {
    return model.getAt(c, p);
  }

  public Map<String, List<Double>> fnslog() {
    return model.fnslog();
  }
}
