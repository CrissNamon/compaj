package com.hiddenproject.compaj.example;

import java.util.function.Supplier;
import com.hiddenproject.compaj.annotation.Bind;
import com.hiddenproject.compaj.annotation.ModelBean;
import com.hiddenproject.compaj.annotation.ModelConstant;
import com.hiddenproject.compaj.annotation.WatchModel;
import com.hiddenproject.compaj.framework.WatchType;

@ModelBean(value = "SIR",
  constants = {
    @ModelConstant(name = "a", value = 1.0)
  }
)
public class ModelConfiguration {

  @WatchModel(type = WatchType.COMPARTMENT, name = "S")
  private Double S;

  @Bind(compartment = "S", initial = 10000.0)
  public Supplier<Double> calculateS() {
    return () -> 0.0 * S;
  }
}
