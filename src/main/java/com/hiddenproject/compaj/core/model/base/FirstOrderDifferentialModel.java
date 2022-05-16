package com.hiddenproject.compaj.core.model.base;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.hiddenproject.compaj.core.data.Equation;
import com.hiddenproject.compaj.core.model.Model;
import com.hiddenproject.compaj.core.model.VoidSupplier;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;

public class FirstOrderDifferentialModel implements Model<String>, FirstOrderDifferentialEquations {

  public static final FirstOrderIntegrator DEFAULT_INTEGRATOR = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);

  private final BaseModel baseModel;
  private FirstOrderIntegrator integrator;

  public FirstOrderDifferentialModel(String name) {
    this.baseModel = new BaseModel(name);
    this.baseModel.equationMap = new LinkedHashMap<>();
    integrator = DEFAULT_INTEGRATOR;
  }

  @Override
  public int getDimension() {
    return eqs().size();
  }

  @Override
  public void computeDerivatives(double v, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
    int i = 0;
    for(Equation<String, Double> entry : eqs().values()) {
      yDot[i] = entry.g();
      i++;
    }
    i = 0;
    for(Equation<String, Double> entry : eqs().values()) {
      ad(entry.getName(), y[i]);
      i++;
    }
  }

  @Override
  public boolean a(Equation<String, Double> e, Double... data) {
    return baseModel.a(e, data);
  }

  @Override
  public boolean a(Equation<String, Double> e, RealVector data) {
    return baseModel.a(e, data);
  }

  @Override
  public List<Equation<String, Double>> a(List<String> label) {
    return baseModel.a(label);
  }

  @Override
  public List<Equation<String, Double>> a(List<String> label, Double... initializer) {
    return baseModel.a(label, initializer);
  }

  @Override
  public List<Equation<String, Double>> a(List<String> label, List<Double> initializer) {
    return baseModel.a(label, initializer);
  }

  @Override
  public void ad(String variable, Double... data) {
    baseModel.ad(variable, data);
  }

  @Override
  public Map<String, Equation<String, Double>> eqs() {
    return baseModel.eqs();
  }

  @Override
  public Map<String, List<Double>> eqslog() {
    return baseModel.eqslog();
  }

  @Override
  public boolean isCase(Equation<String, Double> eq) {
    return eqs().containsKey(eq.getName());
  }

  @Override
  public boolean isCase(String eq) {
    return eqs().containsKey(eq);
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
  public void compute() {
    compute(0d, 100d);
  }

  @Override
  public String toString() {
    return baseModel.toString();
  }

  public void compute(Double lowBound, Double highBound) {
    double[] y = new double[eqs().size()];
    int i = 0;
    for(Equation<String, Double> entry : eqs().values()) {
      y[i] = v(entry.getName());
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
  public void bu(VoidSupplier binder) {
    baseModel.bu(binder);
  }
}
