package com.hiddenproject.compaj.example;

import java.util.function.Supplier;
import com.hiddenproject.compaj.framework.annotation.Bind;
import com.hiddenproject.compaj.framework.annotation.ModelConfiguration;
import com.hiddenproject.compaj.framework.annotation.ModelConstant;
import com.hiddenproject.compaj.framework.annotation.WatchVariable;

@ModelConfiguration(name = "SIR",
  constants = {
    @ModelConstant(name = "a", value = 1.0)
  }
)
public class SIRModel {

  @WatchVariable("S")
  private Double S;

  @Bind(variable = "S", initial = 10000.0)
  public Supplier<Double> calculateS() {
    return () -> 0.0 * S;
  }

  @Bind(variable = "I", initial = 1000.0)
  public String calculateI() {
    return "sqrt(3) + S(1)";
  }
}
