package com.hiddenproject.compaj.core.model.epidemic;

import java.util.HashMap;
import java.util.Map;
import com.hiddenproject.compaj.core.data.DataGetter;
import com.hiddenproject.compaj.core.data.Variable;
import com.hiddenproject.compaj.core.model.base.FirstOrderDifferentialModel;
import org.apache.commons.math3.ode.FirstOrderIntegrator;

public class BaseSIRModel implements DataGetter<Double> {

  private final FirstOrderDifferentialModel model;

  private final Map<String, Double> consts;

  private final double initA = 0.04;
  private final double initB = 0.5;

  private Variable<String, Double> S;
  private Variable<String, Double> I;
  private Variable<String, Double> R;

  public BaseSIRModel(Double sInit, Double iInit, Double rInit) {
    this("BaseSIRModel", sInit, iInit, rInit);
  }

  public BaseSIRModel(String name, Double sInit, Double iInit, Double rInit) {
    model = new FirstOrderDifferentialModel(name);
    consts = new HashMap<>();
    consts.put("a", initA);
    consts.put("b", initB);
    S = model.a("S", sInit);
    S.b(this::susceptible);
    I = model.a("I", iInit);
    I.b(this::infected);
    R = model.a("R", rInit);
    R.b(this::recovered);
  }

  public void sC(String name, Double data) {
    consts.put(name, data);
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
    return -consts.get("b") * model.v("S") * model.v("I") / (model.v("S") + model.v("I") + model.v("R"));
  }

  public Double infected() {
    return consts.get("b") * model.v("S") * model.v("I")
        / (model.v("S") + model.v("I") + model.v("R"))
        - consts.get("a") * model.v("I");
  }

  public Double recovered() {
    return consts.get("a") * model.v("I");
  }

  @Override
  public Double v(String label) {
    return model.v(label);
  }

  @Override
  public Double v(String label, int position) {
    return model.v(label, position);
  }
}
