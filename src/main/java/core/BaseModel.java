package core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BaseModel implements Model<String, Double> {

  private final String name;

  private final Map<String, Compartment<Double>> constants;
  private final Map<String, Compartment<Double>> compartments;

  private Double currentTime;
  private Supplier<Double> timeUpdater;

  public BaseModel(String name) {
    this.compartments = new HashMap<>();
    this.constants = new HashMap<>();
    this.currentTime = 0.0;
    this.timeUpdater = () -> 0.0;
    this.name = name;
  }

  public BaseModel(String name, Double initialTime) {
    this(name);
    this.currentTime = initialTime;
  }

  public static BaseModel from(Model<String, Double> startModel) {
    BaseModel model = new BaseModel(startModel.getName());
    model.compartments.putAll(startModel.compartments());
    model.constants.putAll(startModel.constants());
    model.currentTime = startModel.time();
    model.timeUpdater = startModel.timeUpdater();
    return model;
  }

  @Override
  public Double time() {
    return currentTime;
  }

  @Override
  public Supplier<Double> timeUpdater() {
    return timeUpdater;
  }

  @Override
  public void bindTime(Supplier<Double> binder) {
    timeUpdater = binder;
  }

  @Override
  public void addCompartment(String compartmentLabel, Double initial) {
    BaseCompartment compartment = new BaseCompartment();
    compartment.setData(initial);
    compartments.put(compartmentLabel, compartment);
  }

  @Override
  public void addConstant(String label, Double initial) {
    BaseCompartment constant = new BaseCompartment();
    constant.setData(initial);
    constants.put(label, constant);
  }

  @Override
  public void bindCompartment(String compartmentLabel, Supplier<Double> binder) {
    compartments.get(compartmentLabel).setFormula(binder);
  }

  public void bindCompartment(String compartmentLabel, final String expression) {
    bindCompartment(compartmentLabel, () -> {
      String formula = expression;
      Map<String, Compartment<Double>> symbols = new HashMap<>(compartments);
      symbols.putAll(constants);
      for (Map.Entry<String, Compartment<Double>> entry : symbols.entrySet()) {
        formula = formula.replaceAll("(?<=[+\\-*/])*("+entry.getKey()+")(?=[+\\-*/])*", String.valueOf(entry.getValue().getData()));
      }
      return ExpressionParser.eval(formula);
    });
  }

  @Override
  public Map<String, Compartment<Double>> compartments() {
    return compartments;
  }

  @Override
  public Map<String, Compartment<Double>> constants() {
    return constants;
  }

  @Override
  public Double compartment(String compartment) {
    return compartments.get(compartment).getData();
  }

  @Override
  public Double constant(String label) {
    return constants.get(label).getData();
  }

  @Override
  public void update() {
    Map<String, Compartment<Double>> tmp = new HashMap<>(compartments());
    for (Map.Entry<String, Compartment<Double>> entry : compartments().entrySet()) {
      Compartment<Double> val = new BaseCompartment();
      val.setData(entry.getValue().getFormula().get());
      val.setFormula(entry.getValue().getFormula());
      tmp.put(entry.getKey(), val);
    }
    compartments.putAll(tmp);
    tmp.clear();
    currentTime += timeUpdater.get();
  }

  @Override
  public String getName() {
    return name;
  }
}
