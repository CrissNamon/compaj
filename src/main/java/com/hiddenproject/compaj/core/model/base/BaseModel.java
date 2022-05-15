package com.hiddenproject.compaj.core.model.base;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import com.hiddenproject.compaj.core.data.Constant;
import com.hiddenproject.compaj.core.data.Variable;
import com.hiddenproject.compaj.core.data.base.BaseVariable;
import com.hiddenproject.compaj.core.model.Model;
import com.hiddenproject.compaj.core.model.VoidSupplier;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;

public class BaseModel implements Model<String, Double> {

  private final String name;

  private Map<String, Constant<String, Double>> constants;
  protected Map<String, Variable<String, Double>> variables;
  private Map<String, List<Double>> variablesLog;

  private VoidSupplier updateBinder;

  public BaseModel(String name) {
    this.variables = new HashMap<>();
    this.constants = new HashMap<>();
    this.variablesLog = new HashMap<>();
    this.name = name;
    this.updateBinder = this::defaultUpdater;
  }

  public static BaseModel from(Model<String, Double> startModel) {
    BaseModel model = new BaseModel(startModel.getName());
    model.variables = new HashMap<>(startModel.variables());
    model.constants = new HashMap<>(startModel.constants());
    model.variablesLog = new HashMap<>(startModel.variablesLog());
    return model;
  }

  @Override
  public Variable<String, Double> a(String variable, Double data) {
    Variable<String, Double> v = new BaseVariable(variable, data);
    variables.put(variable, v);
    variablesLog.put(variable, new ArrayList<>());
    variablesLog.get(variable).add(data);
    return v;
  }

  @Override
  public Variable<String, Double> a(String label) {
    return a(label, 0.0);
  }

  @Override
  public List<Variable<String, Double>> a(List<String> label, List<Double> data) {
    List<Variable<String, Double>> vars = new ArrayList<>();
    IntStream.range(0, label.size())
        .forEach(i -> vars.add(
            a(label.get(i), data.get(i))
        ));
    return vars;
  }

  @Override
  public List<Variable<String, Double>> a(List<String> label) {
    List<Variable<String, Double>> vars = new ArrayList<>();
    IntStream.range(0, label.size())
        .forEach(i -> vars.add(
            a(label.get(i))
        ));
    return vars;
  }

  @Override
  public void ad(String variable, Double... data) {
    for(Double d : data) {
      variablesLog.get(variable).add(d);
    }
    variables.get(variable).s(data[data.length-1]);
  }

  @Override
  public void a(Constant<String, Double> constant) {
    constants.putIfAbsent(constant.getName(), constant);
  }

  @Override
  public void b(String variableLabel, Double fixedBinder) {
    variables().get(variableLabel).b(() -> fixedBinder);
  }

  @Override
  public Map<String, Variable<String, Double>> variables() {
    return Collections.unmodifiableMap(variables);
  }

  @Override
  public Map<String, List<Double>> variablesLog() {
    return variablesLog;
  }

  @Override
  public Map<String, Constant<String, Double>> constants() {
    return Collections.unmodifiableMap(constants);
  }

  @Override
  public Double v(String label) {
    return variablesLog.get(label).get(variablesLog.get(label).size()-1);
  }

  @Override
  public Double v(String label, int position) {
    return variablesLog.get(label).get(variablesLog.get(label).size() - position);
  }

  @Override
  public final void b(String variableLabel, Supplier<Double> binder) {
    variables.get(variableLabel).b(binder);
  }

  @Override
  public Double c(String label) {
    return constants.get(label).getData();
  }

  @Override
  public void compute() {
    updateBinder.get();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void bindUpdater(VoidSupplier binder) {
    updateBinder = binder;
  }

  private void defaultUpdater() {
    Map<String, Variable<String, Double>> tmp = new HashMap<>(variables());
    for (Map.Entry<String, Variable<String, Double>> entry : variables().entrySet()) {
      Variable<String, Double> val = new BaseVariable(entry.getKey(), entry.getValue().getBinder().get());
      val.b(entry.getValue().getBinder());
      tmp.put(entry.getKey(), val);
    }
    for (Map.Entry<String, Variable<String, Double>> entry : tmp.entrySet()) {
      variablesLog.get(entry.getKey()).add(entry.getValue().getData());
    }
    variables.putAll(tmp);
    tmp.clear();
  }
}
