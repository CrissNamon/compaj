package tech.hiddenproject.compaj.core.model.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import tech.hiddenproject.compaj.core.data.NamedFunction;
import tech.hiddenproject.compaj.core.model.Model;

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
      equationsLog.get(entry.getKey()).add(entry.getValue().value(data.get(entry.getKey())));
    }
    equationMap.putAll(tmp);
    tmp.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAt(String label) {
    return getAt(label, 1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void a(NamedFunction... fns) {
    for (NamedFunction e : fns) {
      equationMap.putIfAbsent(e.getName(), e);
      equationsLog.putIfAbsent(e.getName(), new ArrayList<>());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void ad(String function, Object... data) {
    for (Object d : data) {
      equationsLog.get(function).add(d);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map fns() {
    return Collections.unmodifiableMap(equationMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map fnslog() {
    return equationsLog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAt(String label, int position) {
    return equationsLog.get(label).get(equationsLog.get(label).size() - position);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void compute(Map<String, Object[]> data) {
    updateBinder.accept(data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void compute() {
    compute(new HashMap<>());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    equationsLog.values().forEach(List::clear);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear(String f) {
    equationsLog.get(f).clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void bu(Consumer<Map<String, Object[]>> binder) {
    updateBinder = binder;
  }

  @Override
  public String toString() {
    return "BaseModel{" + "name='" + name + '\'' + ", equationMap=" + equationMap + '}';
  }
}
