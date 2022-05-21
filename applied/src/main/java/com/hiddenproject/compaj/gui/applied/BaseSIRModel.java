package com.hiddenproject.compaj.gui.applied;

import org.apache.commons.math3.ode.FirstOrderIntegrator;

public class BaseSIRModel {

  private final com.hiddenproject.compaj.gui.core.model.base.FirstOrderDifferentialModel model;

  private final Double a = 0.04;
  private final Double b = 0.4;

  private com.hiddenproject.compaj.gui.core.data.base.RealFunction S;
  private com.hiddenproject.compaj.gui.core.data.base.RealFunction I;
  private com.hiddenproject.compaj.gui.core.data.base.RealFunction R;

  public BaseSIRModel(Double sInit, Double iInit, Double rInit) {
    this("BaseSIRModel", sInit, iInit, rInit);
  }

  public BaseSIRModel(String name, Double sInit, Double iInit, Double rInit) {
    model = new com.hiddenproject.compaj.gui.core.model.base.FirstOrderDifferentialModel(name);
    S = new com.hiddenproject.compaj.gui.core.data.base.RealFunction("S");
    I = new com.hiddenproject.compaj.gui.core.data.base.RealFunction("I");
    R = new com.hiddenproject.compaj.gui.core.data.base.RealFunction("R");
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
    S.b(com.hiddenproject.compaj.gui.core.data.NamedFunction.from(this::susceptible));
    I.b(com.hiddenproject.compaj.gui.core.data.NamedFunction.from(this::infected));
    R.b(com.hiddenproject.compaj.gui.core.data.NamedFunction.from(this::recovered));
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
