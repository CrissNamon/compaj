package com.hiddenproject.compaj.core.model.base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import com.hiddenproject.compaj.core.data.Constant;
import com.hiddenproject.compaj.core.data.Variable;
import com.hiddenproject.compaj.core.model.Model;
import com.hiddenproject.compaj.core.model.VoidSupplier;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class FirstOrderDifferentialModel implements Model<String, Double>, FirstOrderDifferentialEquations {

  public static final FirstOrderIntegrator DEFAULT_INTEGRATOR = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);

  private final BaseModel baseModel;
  private FirstOrderIntegrator integrator;

  public FirstOrderDifferentialModel(String name) {
    this.baseModel = new BaseModel(name);
    this.baseModel.variables = new LinkedHashMap<>();
    integrator = DEFAULT_INTEGRATOR;
  }

  @Override
  public int getDimension() {
    return variables().size();
  }

  @Override
  public void computeDerivatives(double v, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
    int i = 0;
    for(Variable<String, Double> entry : variables().values()) {
      yDot[i] = entry.g();
      i++;
    }
    i = 0;
    for(Variable<String, Double> entry : variables().values()) {
      ad(entry.getName(), y[i]);
      i++;
    }
  }

  @Override
  public Variable<String, Double> a(String label, Double data) {
    return baseModel.a(label, data);
  }

  @Override
  public Variable<String, Double> a(String label) {
    return baseModel.a(label);
  }

  @Override
  public List<Variable<String, Double>> a(List<String> label, List<Double> data) {
    return baseModel.a(label, data);
  }

  @Override
  public List<Variable<String, Double>> a(List<String> label) {
    return baseModel.a(label);
  }

  @Override
  public void ad(String variable, Double... data) {
    baseModel.ad(variable, data);
  }

  @Override
  public void a(Constant<String, Double> constant) {
    baseModel.a(constant);
  }

  @Override
  public void b(String variableLabel, Supplier<Double> binder) {
    baseModel.b(variableLabel, binder);
  }

  @Override
  public void b(String variableLabel, Double fixedBinder) {
    baseModel.b(variableLabel, fixedBinder);
  }

  @Override
  public Map<String, Variable<String, Double>> variables() {
    return baseModel.variables();
  }

  @Override
  public Map<String, List<Double>> variablesLog() {
    return baseModel.variablesLog();
  }

  @Override
  public Map<String, Constant<String, Double>> constants() {
    return baseModel.constants();
  }

  @Override
  public Double v(String label) {
    return baseModel.v(label);
  }

  @Override
  public Double v(String label, int position) {
    return baseModel.v(label, position);
  }

  @Override
  public Double c(String label) {
    return baseModel.c(label);
  }

  @Override
  public void compute() {
    compute(0d, 100d);
  }

  public void compute(Double lowBound, Double highBound) {
    double[] y = new double[variables().size()]; // initial state
    int i = 0;
    for(Variable<String, Double> entry : variables().values()) {
      y[i] = entry.getData();
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
  public void bindUpdater(VoidSupplier binder) {
    baseModel.bindUpdater(binder);
  }
}
