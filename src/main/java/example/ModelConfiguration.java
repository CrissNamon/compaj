package example;

import java.util.function.Supplier;
import annotation.Bind;
import annotation.ModelBean;
import annotation.ModelConstant;
import annotation.WatchModel;
import framework.WatchType;

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
