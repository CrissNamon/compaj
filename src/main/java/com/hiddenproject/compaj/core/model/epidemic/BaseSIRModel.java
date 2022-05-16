package com.hiddenproject.compaj.core.model.epidemic;

import java.util.HashMap;
import java.util.Map;
import com.hiddenproject.compaj.core.data.DataGetter;
import com.hiddenproject.compaj.core.data.Equation;
import com.hiddenproject.compaj.core.data.base.BaseEquation;
import com.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

public class BaseSIRModel implements DataGetter<Double> {

  private final FirstOrderDifferentialModel model;

  private final Double a = 0.04;
  private final Double b = 0.4;

  private Equation<String, Double> S;
  private Equation<String, Double> I;
  private Equation<String, Double> R;

  public BaseSIRModel(Double sInit, Double iInit, Double rInit) {
    this("BaseSIRModel", sInit, iInit, rInit);
  }

  public BaseSIRModel(String name, Double sInit, Double iInit, Double rInit) {
    model = new FirstOrderDifferentialModel(name);
    S = new BaseEquation("S");
    I = new BaseEquation("I");
    R = new BaseEquation("R");
    model.a(S, sInit);
    model.a(I, iInit);
    model.a(R, rInit);
    S.b(this::susceptible);
    I.b(this::infected);
    R.b(this::recovered);
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
    return -b * model.v("S") * model.v("I") / (model.v("S") + model.v("I") + model.v("R"));
  }

  public Double infected() {
    return b * model.v("S") * model.v("I")
        / (model.v("S") + model.v("I") + model.v("R"))
        - a * model.v("I");
  }

  public Double recovered() {
    return a * model.v("I");
  }

  @Override
  public Double v(String label) {
    return model.v(label);
  }

  @Override
  public Double v(String label, int position) {
    return model.v(label, position);
  }

  @Override
  public String toString() {
    return model.toString();
  }
}
