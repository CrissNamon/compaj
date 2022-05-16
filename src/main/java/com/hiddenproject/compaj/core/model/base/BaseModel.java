package com.hiddenproject.compaj.core.model.base;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.hiddenproject.compaj.core.data.Equation;
import com.hiddenproject.compaj.core.data.base.BaseEquation;
import com.hiddenproject.compaj.core.model.Model;
import com.hiddenproject.compaj.core.model.VoidSupplier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.RealVector;

public class BaseModel implements Model<String> {

  private final String name;

  protected Map<String, Equation<String, Double>> equationMap;
  private Map<String, List<Double>> equationsLog;

  private VoidSupplier updateBinder;

  public BaseModel(String name) {
    this.equationMap = new HashMap<>();
    this.equationsLog = new HashMap<>();
    this.name = name;
    this.updateBinder = this::defaultUpdater;
  }

  public static BaseModel from(Model<String> startModel) {
    BaseModel model = new BaseModel(startModel.getName());
    model.equationMap = new HashMap<>(startModel.eqs());
    model.equationsLog = new HashMap<>(startModel.eqslog());
    return model;
  }

  @Override
  public boolean a(Equation<String, Double> e, Double... data) {
    if(equationMap.containsKey(e.getName())) {
      return false;
    }
    equationMap.put(e.getName(), e);
    equationsLog.put(e.getName(), Arrays.stream(data).collect(Collectors.toList()));
    return true;
  }

  @Override
  public boolean a(Equation<String, Double> e, RealVector data) {
    return a(e, ArrayUtils.toObject(data.toArray()));
  }

  @Override
  public List<Equation<String, Double>> a(List<String> label) {
    return label.stream().map(l -> {
      Equation<String, Double> e = new BaseEquation(l);
      a(e);
      ad(l);
      return e;
    })
        .collect(Collectors.toList());
  }

  @Override
  public List<Equation<String, Double>> a(List<String> label, Double... initializer) {
    IntStream.range(0, label.size())
        .forEachOrdered(i -> {
          String l = label.get(i);
          Equation<String, Double> e = new BaseEquation(l);
          a(e);
          ad(l, initializer[i]);
        });
    return new ArrayList<>(eqs().values());
  }

  @Override
  public List<Equation<String, Double>> a(List<String> label, List<Double> initializer) {
    return a(label, initializer.toArray(Double[]::new));
  }

  @Override
  public void ad(String label, Double... data) {
    equationsLog.get(label).addAll(List.of(data));
    //equationMap.get(label).b(data[data.length-1]);
  }

  @Override
  public Map<String, Equation<String, Double>> eqs() {
    return Collections.unmodifiableMap(equationMap);
  }

  @Override
  public Map<String, List<Double>> eqslog() {
    return equationsLog;
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
    return v(label, 1);
  }

  @Override
  public Double v(String label, int position) {
    return equationsLog.get(label).get(equationsLog.get(label).size() - position);
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
  public void bu(VoidSupplier binder) {
    updateBinder = binder;
  }

  @Override
  public String toString() {
    return "BaseModel{" +
        "name='" + name + '\'' +
        ", equationMap=" + equationMap +
        '}';
  }

  private void defaultUpdater() {
    Map<String, Equation<String, Double>> tmp = new HashMap<>(eqs());
    for (Map.Entry<String, Equation<String, Double>> entry : eqs().entrySet()) {
      Equation<String, Double> val = new BaseEquation(entry.getKey(), entry.getValue().getBinder());
      tmp.put(entry.getKey(), val);
    }
    for (Map.Entry<String, Equation<String, Double>> entry : tmp.entrySet()) {
      equationsLog.get(entry.getKey()).add(entry.getValue().g());
    }
    equationMap.putAll(tmp);
    tmp.clear();
  }
}
