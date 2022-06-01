package com.hiddenproject.compaj.core.model.base;

import java.util.*;
import java.util.function.*;
import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.model.*;

public class BaseModel implements Model<String, Object, Object> {

  private final String name;

  protected Map<Object, NamedFunction> equationMap;
  private Map<Object, List<Object>> equationsLog;

  private Consumer<Map<String, Object[]>> updateBinder;

  public BaseModel(String name) {
    this.equationMap = new HashMap<>();
    this.equationsLog = new HashMap<>();
    this.name = name;
    this.updateBinder = this::defaultUpdater;
  }

  private void defaultUpdater(Map<String, Object[]> data) {
    Map<String, NamedFunction> tmp = new HashMap<>(fns());
    for (Map.Entry<String, NamedFunction> entry : tmp.entrySet()) {
      equationsLog
          .get(entry.getKey())
          .add(
              entry.getValue()
                  .value(
                      data.get(entry.getKey()
                      )
                  )
          );
    }
    equationMap.putAll(tmp);
    tmp.clear();
  }

  @Override
  public Object getAt(String label) {
    return getAt(label, 1);
  }

  @Override
  public void a(NamedFunction... fns) {
    for (NamedFunction e : fns) {
      equationMap.putIfAbsent(e.getName(), e);
      equationsLog.putIfAbsent(e.getName(), new ArrayList<>());
    }
  }

  @Override
  public void ad(String label, Object... data) {
    for (Object d : data) {
      equationsLog.get(label).add(d);
    }
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
  public Object getAt(String label, int position) {
    return equationsLog
        .get(label)
        .get(equationsLog.get(label).size() - position);
  }

  @Override
  public void compute(Map<String, Object[]> data) {
    updateBinder.accept(data);
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
  public void bu(Consumer<Map<String, Object[]>> binder) {
    updateBinder = binder;
  }

  @Override
  public String toString() {
    return "BaseModel{" +
        "name='" + name + '\'' +
        ", equationMap=" + equationMap +
        '}';
  }
}
