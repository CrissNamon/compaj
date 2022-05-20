package com.hiddenproject.compaj.core.model.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.hiddenproject.compaj.core.data.NamedFunction;
import com.hiddenproject.compaj.core.model.Model;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class FirstOrderDifferentialModel implements Model<String, String, Double, Double>, FirstOrderDifferentialEquations {

  public static final FirstOrderIntegrator DEFAULT_INTEGRATOR = new DormandPrince853Integrator(1.0e-8, 0.01, 1.0e-10, 1.0e-10);

  private final BaseModel baseModel;
  private FirstOrderIntegrator integrator;
  private boolean f;

  public FirstOrderDifferentialModel(String name) {
    this.baseModel = new BaseModel(name);
    this.baseModel.equationMap = new LinkedHashMap<>();
    integrator = DEFAULT_INTEGRATOR;
  }

  @Override
  public int getDimension() {
    return fns().size();
  }

  @Override
  public void computeDerivatives(double v, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
    int i = 0;
    for(NamedFunction<String, Double, Double> entry : fns().values()) {
      yDot[i] = entry.value(new Double[]{});
      i++;
    }
    i = 0;
    for(NamedFunction<String, Double, Double> entry : fns().values()) {
      ad(entry.getName(), y[i]);
      i++;
    }
  }

  @Override
  public boolean a(NamedFunction<String, Double, Double> e) {
    return baseModel.a(e);
  }
/*
  @Override
  public boolean a(NamedFunction<String, Double, Double> e, RealVector data) {
    return baseModel.a(e, data);
  }

 */

  @Override
  public void ad(String variable, Double... data) {
    baseModel.ad(variable, data);
  }

  @Override
  public Map<String, NamedFunction<String, Double, Double>> fns() {
    return baseModel.fns();
  }

  @Override
  public Map<String, List<Double>> fnslog() {
    return baseModel.fnslog();
  }

  @Override
  public boolean isCase(NamedFunction<String, Double, Double> eq) {
    return fns().containsKey(eq.getName());
  }

  @Override
  public boolean isCase(String eq) {
    return fns().containsKey(eq);
  }

  @Override
  public Double getAt(String label) {
    return baseModel.getAt(label);
  }

  @Override
  public Double getAt(String label, int position) {
    return baseModel.getAt(label, position);
  }

  @Override
  public void compute(Map<String, Double[]> data) {
    compute(0d, 100d);
  }

  @Override
  public void compute() {
    compute(new HashMap<>());
  }

  @Override
  public String toString() {
    return baseModel.toString();
  }

  public void compute(Double lowBound, Double highBound) {
    double[] y = new double[fns().size()];
    int i = 0;
    for (NamedFunction<String, Double, Double> entry : fns().values()) {
      y[i] = getAt(entry.getName());
      i++;
    }
    integrator.integrate(this, lowBound, y, highBound, y);
  }

  public void i(FirstOrderIntegrator firstOrderIntegrator) {
    integrator = firstOrderIntegrator;
  }

  @Override
  public String getName() {
    return baseModel.getName();
  }

  @Override
  public void clear() {
    baseModel.clear();
  }

  @Override
  public void clear(String f) {
    baseModel.clear(f);
  }

  @Override
  public void bu(Consumer<Map<String, Double[]>> binder) {
    baseModel.bu(binder);
  }
}
