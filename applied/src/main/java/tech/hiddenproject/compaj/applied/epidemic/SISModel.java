package tech.hiddenproject.compaj.applied.epidemic;

import java.util.List;
import java.util.Map;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import tech.hiddenproject.compaj.core.data.NamedFunction;
import tech.hiddenproject.compaj.core.data.base.RealFunction;
import tech.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;

public class SISModel {

  private final FirstOrderDifferentialModel model;
  private final RealFunction S;
  private final RealFunction I;
  private final Double sInit;
  private final Double iInit;
  private Double a = 0.04;
  private Double b = 0.4;

  public SISModel(Double sInit, Double iInit) {
    this("SIRModel", sInit, iInit);
  }

  public SISModel(String name, Double sInit, Double iInit) {
    this.sInit = sInit;
    this.iInit = iInit;
    model = new FirstOrderDifferentialModel(name);
    model.i(new EulerIntegrator(1));
    S = new RealFunction("S");
    I = new RealFunction("I");
    model.a(S, I);
    model.ad("S", sInit);
    model.ad("I", iInit);
    S.b(NamedFunction.from(this::susceptible));
    I.b(NamedFunction.from(this::infected));
  }

  private Double susceptible() {
    return -b * model.getAt("S") * model.getAt("I") / (model.getAt("S") + model.getAt("I"));
  }

  private Double infected() {
    return b * model.getAt("S") * model.getAt("I") / (model.getAt("S") + model.getAt("I"))
        - a * model.getAt("I");
  }

  public SISModel withAlpha(Double value) {
    a = value;
    return this;
  }

  public SISModel withBetta(Double value) {
    b = value;
    return this;
  }

  public void compute(Double lowBound, Double highBound) {
    clear();
    model.ad("S", sInit);
    model.ad("I", iInit);
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
