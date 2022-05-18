package com.hiddenproject.compaj.core.model.base;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.hiddenproject.compaj.core.data.NamedFunction;
import com.hiddenproject.compaj.core.data.base.RealFunction;
import com.hiddenproject.compaj.core.model.Model;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.RealVector;

public class BaseModel implements Model<String, String, Double, Double> {

  private final String name;

  protected Map<String, NamedFunction<String, Double, Double>> equationMap;
  private Map<String, List<Double>> equationsLog;

  private Consumer<Map<String, Double[]>> updateBinder;

  public BaseModel(String name) {
    this.equationMap = new HashMap<>();
    this.equationsLog = new HashMap<>();
    this.name = name;
    this.updateBinder = this::defaultUpdater;
  }

  public static BaseModel from(Model<String, String, Double, Double> startModel) {
    BaseModel model = new BaseModel(startModel.getName());
    model.equationMap = new HashMap<>(startModel.fns());
    model.equationsLog = new HashMap<>(startModel.fnslog());
    return model;
  }

  @Override
  public boolean a(NamedFunction<String, Double, Double> e, Double... data) {
    if(equationMap.containsKey(e.getName())) {
      return false;
    }
    equationMap.put(e.getName(), e);
    equationsLog.put(e.getName(), Arrays.stream(data).collect(Collectors.toList()));
    return true;
  }

  @Override
  public boolean a(NamedFunction<String, Double, Double> e, RealVector data) {
    return a(e, ArrayUtils.toObject(data.toArray()));
  }

  @Override
  public List<NamedFunction<String, Double, Double>> a(List<String> label) {
    return label.stream().map(l -> {
      NamedFunction<String, Double, Double> e = new RealFunction(l);
      a(e);
      ad(l);
      return e;
    })
        .collect(Collectors.toList());
  }

  @Override
  public List<NamedFunction<String, Double, Double>> a(List<String> label, Double... initializer) {
    IntStream.range(0, label.size())
        .forEachOrdered(i -> {
          String l = label.get(i);
          NamedFunction<String, Double, Double> e = new RealFunction(l);
          a(e);
          ad(l, initializer[i]);
        });
    return new ArrayList<>(fns().values());
  }

  @Override
  public List<NamedFunction<String, Double, Double>> a(List<String> label, List<Double> initializer) {
    return a(label, initializer.toArray(Double[]::new));
  }

  @Override
  public void ad(String label, Double... data) {
    equationsLog.get(label).addAll(List.of(data));
    //equationMap.get(label).b(data[data.length-1]);
  }

  @Override
  public Map<String, NamedFunction<String, Double, Double>> fns() {
    return Collections.unmodifiableMap(equationMap);
  }

  @Override
  public Map<String, List<Double>> fnslog() {
    return equationsLog;
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
  public void putAt(String label, NamedFunction<String, Double, Double> e) {
    a(e);
  }

  @Override
  public Double getAt(String label) {
    return getAt(label, 1);
  }

  @Override
  public Double getAt(String label, int position) {
    return equationsLog.get(label).get(equationsLog.get(label).size() - position);
  }

  @Override
  public void call(Map<String, Double[]> data) {
    updateBinder.accept(data);
  }

  @Override
  public void call() {
    call(new HashMap<>());
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void clear() {
    equationsLog.values().forEach(List::clear);
  }

  @Override
  public void clear(String f) {
    equationsLog.get(f).clear();
  }

  @Override
  public void bu(Consumer<Map<String, Double[]>> binder) {
    updateBinder = binder;
  }

  @Override
  public String toString() {
    return "BaseModel{" +
        "name='" + name + '\'' +
        ", equationMap=" + equationMap +
        '}';
  }

  private void defaultUpdater(Map<String, Double[]> data) {
    Map<String, NamedFunction<String, Double, Double>> tmp = new HashMap<>(fns());
    for (Map.Entry<String, NamedFunction<String, Double, Double>> entry : tmp.entrySet()) {
      equationsLog.get(entry.getKey()).add(entry.getValue().value(data.get(entry.getKey())));
    }
    equationMap.putAll(tmp);
    tmp.clear();
  }
}
