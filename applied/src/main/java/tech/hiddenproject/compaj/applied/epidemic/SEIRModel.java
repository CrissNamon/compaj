package tech.hiddenproject.compaj.applied.epidemic;

import java.util.List;
import java.util.Map;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import tech.hiddenproject.compaj.core.data.NamedFunction;
import tech.hiddenproject.compaj.core.data.base.RealFunction;
import tech.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;

public class SEIRModel {

  private final FirstOrderDifferentialModel model;
  private final RealFunction S;
  private final RealFunction E;
  private final RealFunction I;
  private final RealFunction R;
  private final Double sInit;
  private final Double eInit;
  private final Double iInit;
  private final Double rInit;
  private Double y = 0.04;
  private Double a = 0.01;
  private Double b = 0.4;
  private Double l = 0.1;
  private Double n = 0.1;

  public SEIRModel(Double sInit, Double eInit, Double iInit, Double rInit) {
    this("SEIRModel", sInit, eInit, iInit, rInit);
  }

  public SEIRModel(String name, Double sInit, Double eInit, Double iInit, Double rInit) {
    this.sInit = sInit;
    this.eInit = eInit;
    this.iInit = iInit;
    this.rInit = rInit;
    model = new FirstOrderDifferentialModel(name);
    model.i(new EulerIntegrator(1));
    S = new RealFunction("S");
    E = new RealFunction("E");
    I = new RealFunction("I");
    R = new RealFunction("R");
    model.a(S, E, I, R);
    model.ad("S", sInit);
    model.ad("E", eInit);
    model.ad("I", iInit);
    model.ad("R", rInit);
    S.b(NamedFunction.from(this::susceptible));
    E.b(NamedFunction.from(this::exposed));
    I.b(NamedFunction.from(this::infected));
    R.b(NamedFunction.from(this::recovered));
  }

  private Double susceptible() {
    return n * total() - n * model.getAt("S") - b * model.getAt("S") * model.getAt("I") / total();
  }

  private Double exposed() {
    return b * model.getAt("S") * model.getAt("I") / total() - (n * a) * model.getAt("E");
  }

  private Double infected() {
    return a * model.getAt("E") - (y + n) * model.getAt("I");
  }

  private Double recovered() {
    return y * model.getAt("I") - n * model.getAt("R");
  }

  private Double total() {
    return model.getAt("S") + model.getAt("I") + model.getAt("R") + model.getAt("E");
  }

  public SEIRModel withAlpha(Double value) {
    a = value;
    return this;
  }

  public SEIRModel withBetta(Double value) {
    b = value;
    return this;
  }

  public SEIRModel withGamma(Double value) {
    y = value;
    return this;
  }

  public SEIRModel withLambda(Double value) {
    l = value;
    return this;
  }

  public SEIRModel withNu(Double value) {
    n = value;
    return this;
  }

  public void compute(Double lowBound, Double highBound) {
    clear();
    model.ad("S", sInit);
    model.ad("E", eInit);
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
