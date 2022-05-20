package com.hiddenproject.compaj.applied;

import com.hiddenproject.compaj.core.data.NamedFunction;
import com.hiddenproject.compaj.core.data.base.RealFunction;
import com.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

public class BaseSIRModel {

  private final FirstOrderDifferentialModel model;

  private final Double a = 0.04;
  private final Double b = 0.4;

  private RealFunction S;
  private RealFunction I;
  private RealFunction R;

  public BaseSIRModel(Double sInit, Double iInit, Double rInit) {
    this("BaseSIRModel", sInit, iInit, rInit);
  }

  public BaseSIRModel(String name, Double sInit, Double iInit, Double rInit) {
    model = new FirstOrderDifferentialModel(name);
    S = new RealFunction("S");
    I = new RealFunction("I");
    R = new RealFunction("R");
    /*
    model.a(S, sInit);
    model.a(I, iInit);
    model.a(R, rInit);

     */
    model.a(S);
    model.a(I);
    model.a(R);
    model.ad("S", sInit);
    model.ad("I", iInit);
    model.ad("R", rInit);
    S.b(NamedFunction.from(this::susceptible));
    I.b(NamedFunction.from(this::infected));
    R.b(NamedFunction.from(this::recovered));
  }

  public void compute() {
    compute(0d, 100d);
  }

  public void i(FirstOrderIntegrator firstOrderIntegrator) {
    model.i(firstOrderIntegrator);
  }

  public void compute(Double lowBound, Double highBound) {
    model.compute(lowBound, highBound);
  }

  public Double susceptible() {
    return -b * model.getAt("S") * model.getAt("I") / (model.getAt("S") + model.getAt("I") + model.getAt("R"));
  }

  public Double infected() {
    return b * model.getAt("S") * model.getAt("I")
        / (model.getAt("S") + model.getAt("I") + model.getAt("R"))
        - a * model.getAt("I");
  }

  public Double recovered() {
    return a * model.getAt("I");
  }

  public void clear() {
    model.clear();
  }

  @Override
  public String toString() {
    return model.toString();
  }

  public Double getAt(String c){
    return model.getAt(c);
  }

  private Double getAt(String c, int p) {
    return model.getAt(c, p);
  }
}
