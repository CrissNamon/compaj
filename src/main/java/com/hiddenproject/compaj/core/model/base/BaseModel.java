package com.hiddenproject.compaj.core.model.base;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hiddenproject.compaj.core.expressor.ExpressionParser;
import com.hiddenproject.compaj.core.data.base.BaseVariable;
import com.hiddenproject.compaj.core.data.Constant;
import com.hiddenproject.compaj.core.data.Variable;
import com.hiddenproject.compaj.core.model.Model;

public class BaseModel implements Model<String, Double> {

  private final String name;

  private Map<String, Constant<String, Double>> constants;
  private Map<String, Variable<String, Double>> variables;
  private Map<String, List<Double>> variablesLog;

  public BaseModel(String name) {
    this.variables = new HashMap<>();
    this.constants = new HashMap<>();
    this.variablesLog = new HashMap<>();
    this.name = name;
  }

  public static BaseModel from(Model<String, Double> startModel) {
    BaseModel model = new BaseModel(startModel.getName());
    model.variables = new HashMap<>(startModel.variables());
    model.constants = new HashMap<>(startModel.constants());
    model.variablesLog = new HashMap<>(startModel.variablesLog());
    return model;
  }

  @Override
  public void addVariable(Variable<String, Double> variable) {
    variables.putIfAbsent(variable.getName(), variable);
    variablesLog.putIfAbsent(variable.getName(), new ArrayList<>());
    variablesLog.get(variable.getName()).add(variable.getData());
  }

  @Override
  public void setVariable(String variable, Double data, int position) {
    if(position == variablesLog.get(variable).size()) {
      variablesLog.get(variable).add(data);
    } else {
      variablesLog.get(variable).set(position, data);
    }
  }

  @Override
  public void addConstant(Constant<String, Double> constant) {
    constants.putIfAbsent(constant.getName(), constant);
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
  public Double variable(String label) {
    return variables.get(label).getData();
  }

  @Override
  public Double variable(String label, int position) {
    return variablesLog.get(label).get(position);
  }

  public void bindVariable(String variableLabel, final String expression) {
   variables.get(variableLabel).bind(() -> {
     Map<String, Constant<String, Double>> symbols = new HashMap<>(constants);
     symbols.putAll(variables);
     String formula = expression;
     for(Map.Entry<String, Constant<String, Double>> entry : symbols.entrySet()) {
       Pattern pattern = Pattern.compile("(?<=[+\\-*/])*("+entry.getKey()+"(?>\\([0-9]*\\))?)(?=[+\\-*/])*");
       Matcher matcher = pattern.matcher(formula);
       while(matcher.find()) {
         String subExpr = matcher.group();
         if(subExpr.endsWith(")")) {
           int start = subExpr.indexOf("(");
           int end = subExpr.lastIndexOf(")");
           int varNum = Integer.parseInt(subExpr.substring(start+1, end));
           formula = formula.replace(subExpr, variable(entry.getKey(), varNum).toString());
         } else {
           formula = formula.replaceFirst(subExpr, symbols.get(subExpr).getData().toString());
         }
       }
     }
     return ExpressionParser.eval(formula);
   });
  }


  @Override
  public Double constant(String label) {
    return constants.get(label).getData();
  }

  @Override
  public void update() {
    Map<String, Variable<String, Double>> tmp = new HashMap<>(variables());
    for (Map.Entry<String, Variable<String, Double>> entry : variables().entrySet()) {
      Variable<String, Double> val = new BaseVariable(entry.getKey(), entry.getValue().getBinder().get());
      val.bind(entry.getValue().getBinder());
      tmp.put(entry.getKey(), val);
      variablesLog.get(entry.getKey()).add(val.getData());
    }
    variables.putAll(tmp);
    tmp.clear();
  }

  @Override
  public String getName() {
    return name;
  }
}
