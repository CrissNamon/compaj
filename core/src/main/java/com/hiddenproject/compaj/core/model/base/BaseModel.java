package com.hiddenproject.compaj.core.model.base;

import java.util.*;
import java.util.function.Consumer;
import com.hiddenproject.compaj.core.data.NamedFunction;
import com.hiddenproject.compaj.core.model.Model;

public class BaseModel implements Model<String, String, Double, Double> {

  private final String name;

  protected Map<Object, NamedFunction> equationMap;
  private Map<Object, List<Object>> equationsLog;

  private Consumer<Map<String, Double[]>> updateBinder;

  public BaseModel(String name) {
    this.equationMap = new HashMap<>();
    this.equationsLog = new HashMap<>();
    this.name = name;
    this.updateBinder = this::defaultUpdater;
  }
/*
  public static BaseModel from(Model<String, String, Double, Double> startModel) {
    BaseModel model = new BaseModel(startModel.getName());
    model.equationMap = new HashMap<>(startModel.fns());
    model.equationsLog = new HashMap<>(startModel.fnslog());
    return model;
  }

 */

  @Override
  public boolean a(NamedFunction e) {
    if(equationMap.containsKey(e.getName())) {
      return false;
    }
    equationMap.put(e.getName(), e);
    equationsLog.put(e.getName(), new ArrayList<>());
    //equationsLog.put(e.getName(), Arrays.stream(data).collect(Collectors.toList()));
    return true;
  }

  /*
  @Override
  public boolean a(NamedFunction<String, Double, Double> e, RealVector data) {
    return a(e, ArrayUtils.toObject(data.toArray()));
  }

   */

  @Override
  public void ad(String label, Double... data) {
    equationsLog.get(label).addAll(List.of(data));
    //equationMap.get(label).b(data[data.length-1]);
  }

  @Override
  public Map fns() {
    return Collections.unmodifiableMap(equationMap);
  }

  @Override
  public Map fnslog() {
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
  public Double getAt(String label) {
    return getAt(label, 1);
  }

  @Override
  public Double getAt(String label, int position) {
    return (Double)equationsLog.get(label).get(equationsLog.get(label).size() - position);
  }

  @Override
  public void compute(Map<String, Double[]> data) {
    try {
      updateBinder.accept(data);
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void compute() {
    compute(new HashMap<>());
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
    Map<String, NamedFunction> tmp = new HashMap<>(fns());
    for (Map.Entry<String, NamedFunction> entry : tmp.entrySet()) {
      equationsLog
          .get(
              entry
                  .getKey()
          )
          .add(
              entry.getValue()
                  .value(data
                      .get(entry
                          .getKey())
          )
      );
    }
    equationMap.putAll(tmp);
    tmp.clear();
  }
}
