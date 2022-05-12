package example;

import annotation.Simulation;
import core.BaseModel;

@Simulation(epidemic = "Test")
public class Main {

  public static void main(String[] args) {
    BaseModel sir = new BaseModel("SIR", 0.0);
    sir.bindTime(() -> 1.0);
    sir.addConstant("a", 0.05);
    sir.addConstant("b", 1E-4);
    sir.addCompartment("S", 10000.0);
    sir.addCompartment("I", 1000.0);
    sir.addCompartment("R", 0.0);
    sir.bindCompartment("S", () -> -sir.constant("b") * sir.compartment("S") * sir.compartment("I") + sir.compartment("S"));
    sir.bindCompartment("I", () -> sir.constant("b") * sir.compartment("S") * sir.compartment("I") - sir.constant("a") * sir.compartment("I") + sir.compartment("I"));
    sir.bindCompartment("R", "a * I + R");
    sir.update();
    System.out.println("S1 = " + sir.compartment("S"));
    System.out.println("I1 = " + sir.compartment("I"));
    System.out.println("R1 = " + sir.compartment("R"));
    System.out.println("T = " + sir.time());
    sir.update();
    System.out.println("S2 = " + sir.compartment("S"));
    System.out.println("I2 = " + sir.compartment("I"));
    System.out.println("R2 = " + sir.compartment("R"));
    System.out.println("T = " + sir.time());

    BaseModel seir = BaseModel.from(sir);
    seir.addCompartment("E", 0.0);
    seir.bindCompartment("E", "2 * I");
    seir.update();
    System.out.println("S3 = " + seir.compartment("S"));
    System.out.println("E3 = " + seir.compartment("E"));
    System.out.println("I3 = " + seir.compartment("I"));
    System.out.println("R3 = " + seir.compartment("R"));
    System.out.println("T = " + seir.time());
  }
}
