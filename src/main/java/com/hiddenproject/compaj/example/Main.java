package com.hiddenproject.compaj.example;

import com.hiddenproject.compaj.annotation.Simulation;
import com.hiddenproject.compaj.core.data.base.BaseConstant;
import com.hiddenproject.compaj.core.model.base.BaseModel;
import com.hiddenproject.compaj.core.data.base.BaseVariable;
import com.hiddenproject.compaj.core.expressor.ExpressionParser;

@Simulation(epidemic = "Test")
public class Main {

  public static void main(String[] args) {
    BaseModel sir = new BaseModel("SIR");
    BaseConstant a = new BaseConstant("a", 0.05);
    BaseConstant b = new BaseConstant("b", 1E-4);
    sir.addConstant(a);
    sir.addConstant(b);
    BaseVariable S = new BaseVariable("S", 10000.0);
    S.bind(() -> -sir.constant("b") * sir.variable("S") * sir.variable("I") + sir.variable("S"));
    sir.addVariable(S);
    BaseVariable I = new BaseVariable("I", 1000.0);
    I.bind(() -> sir.constant("b") * sir.variable("S") * sir.variable("I") - sir.constant("a") * sir.variable("I") + sir.variable("I"));
    sir.addVariable(I);
    BaseVariable R = new BaseVariable("R", 0.0);
    sir.addVariable(R);
    sir.bindVariable("R", "a * I + R");
    sir.update();
    System.out.println("S1 = " + sir.variable("S"));
    System.out.println("I1 = " + sir.variable("I"));
    System.out.println("R1 = " + sir.variable("R"));
    sir.update();
    System.out.println("S2 = " + sir.variable("S"));
    System.out.println("I2 = " + sir.variable("I"));
    System.out.println("R2 = " + sir.variable("R"));

    BaseModel seir = BaseModel.from(sir);
    BaseVariable E = new BaseVariable("E", 0.0);
    seir.addVariable(E);
    seir.bindVariable("E", "2 * I");
    seir.update();
    System.out.println("S3 = " + seir.variable("S"));
    System.out.println("E3 = " + seir.variable("E"));
    System.out.println("I3 = " + seir.variable("I"));
    System.out.println("R3 = " + seir.variable("R"));

    BaseModel testModel = new BaseModel("Test");
    BaseVariable testVar = new BaseVariable("A", 0.0);
    testModel.addVariable(testVar);
    testModel.bindVariable("A", "sqrt(A+A(1)+A(2)+sqrt(4))*ln(10)+max(1,2)");
    ExpressionParser.loadFunction("myFunc", "x(0)+x(1)");
    testModel.setVariable("A", 1.0, 1);
    testModel.setVariable("A", 2.0, 2);
    testModel.update();
    System.out.println("A = " + testModel.variable("A"));
    BaseVariable h = new BaseVariable("H", 1.3);
    System.out.println(h.getData());
    h.setData(1.4);
    System.out.println(h.getData());
  }
}
