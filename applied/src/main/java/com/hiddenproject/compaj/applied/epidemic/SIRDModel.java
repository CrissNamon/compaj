package com.hiddenproject.compaj.applied.epidemic;

import java.util.*;
import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.data.base.*;
import com.hiddenproject.compaj.core.model.base.*;
import org.apache.commons.math3.ode.*;
import org.apache.commons.math3.ode.nonstiff.*;

public class SIRDModel {

  private final FirstOrderDifferentialModel model;

  private Double a = 0.04;
  private Double b = 0.4;
  private Double n = 0.1;

  private RealFunction S;
  private RealFunction I;
  private RealFunction R;
  private RealFunction D;

  private Double sInit;
  private Double iInit;
  private Double rInit;
  private Double dInit;

  public SIRDModel(Double sInit, Double iInit, Double rInit, Double dInit) {
    this("SIRDModel", sInit, iInit, rInit, dInit);
  }

  public SIRDModel(String name, Double sInit, Double iInit, Double rInit, Double dInit) {
    this.sInit = sInit;
    this.iInit = iInit;
    this.rInit = rInit;
    this.dInit = dInit;
    model = new FirstOrderDifferentialModel(name);
    model.i(new EulerIntegrator(1));
    S = new RealFunction("S");
    I = new RealFunction("I");
    R = new RealFunction("R");
    D = new RealFunction("D");
    model.a(S, I, R, D);
    model.ad("S", sInit);
    model.ad("I", iInit);
    model.ad("R", rInit);
    model.ad("D", dInit);
    S.b(NamedFunction.from(this::susceptible));
    I.b(NamedFunction.from(this::infected));
    R.b(NamedFunction.from(this::recovered));
    D.b(NamedFunction.from(this::deceased));
  }

  private Double susceptible() {
    return - b * model.getAt("S") * model.getAt("I") / total();
  }

  private Double infected() {
    return b * model.getAt("S") * model.getAt("I")
        / total()
        - a * model.getAt("I") - n * model.getAt("I");
  }

  private Double recovered() {
    return a * model.getAt("I");
  }

  private Double deceased() {
    return n * model.getAt("I");
  }

  private Double total() {
    return (model.getAt("S") + model.getAt("I") + model.getAt("R") + model.getAt("D"));
  }

  public SIRDModel withAlpha(Double value) {
    a = value;
    return this;
  }

  public SIRDModel withBetta(Double value) {
    b = value;
    return this;
  }

  public SIRDModel withN(Double value) {
    n = value;
    return this;
  }

  public void compute(Double lowBound, Double highBound) {
    clear();
    model.ad("S", sInit);
    model.ad("I", iInit);
    model.ad("R", rInit);
    model.ad("D", dInit);
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
