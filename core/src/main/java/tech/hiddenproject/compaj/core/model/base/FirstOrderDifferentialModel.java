package tech.hiddenproject.compaj.core.model.base;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import tech.hiddenproject.compaj.core.data.NamedFunction;

/**
 * Model to compute models with First Order Differential.
 */
public class FirstOrderDifferentialModel implements FirstOrderDifferentialEquations {

  public static final FirstOrderIntegrator DEFAULT_INTEGRATOR =
      new DormandPrince853Integrator(1.0e-8, 0.01, 1.0e-10, 1.0e-10);

  private final BaseModel baseModel;
  private FirstOrderIntegrator integrator;
  private long iterator;

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
  public void computeDerivatives(double v, double[] y, double[] yDot)
      throws MaxCountExceededException, DimensionMismatchException {
    int i = 0;
    for (NamedFunction<String, Double, Double> entry : fns().values()) {
      yDot[i] = entry.value();
      i++;
    }
    if (iterator > 0) {
      i = 0;
      for (NamedFunction<String, Double, Double> entry : fns().values()) {
        ad(entry.getName(), y[i]);
        i++;
      }
    }
    iterator++;
  }

  public void ad(String variable, Double... data) {
    baseModel.ad(variable, data);
  }

  public Map<String, NamedFunction<String, Double, Double>> fns() {
    return baseModel.fns();
  }

  @Override
  public String toString() {
    return baseModel.toString();
  }

  @SafeVarargs
  public final void a(NamedFunction<String, Double, Double>... e) {
    baseModel.a(e);
  }

  public void i(FirstOrderIntegrator firstOrderIntegrator) {
    integrator = firstOrderIntegrator;
  }

  public Map<String, List<Double>> fnslog() {
    return baseModel.fnslog();
  }

  public Double getAt(String label, int position) {
    return (Double) baseModel.getAt(label, position);
  }

  public void compute(Double lowBound, Double highBound) {
    iterator = 0;
    double[] y = new double[fns().size()];
    int i = 0;
    for (NamedFunction<String, Double, Double> entry : fns().values()) {
      y[i] = getAt(entry.getName());
      i++;
    }
    integrator.integrate(this, lowBound, y, highBound, y);
  }

  public Double getAt(String label) {
    return (Double) baseModel.getAt(label);
  }

  public String getName() {
    return baseModel.getName();
  }

  public void clear() {
    baseModel.clear();
  }

  public void clear(String f) {
    baseModel.clear(f);
  }
}
