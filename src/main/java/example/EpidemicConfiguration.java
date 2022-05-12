package example;

import annotation.EndCondition;
import annotation.Epidemic;
import annotation.Phase;
import annotation.WatchPhase;
import core.BaseModel;

@Epidemic(
    name = "Test",
    phases = {
        @Phase(model = "SIR", order = 1),
        @Phase(model = "SEIR", order = 2)
    }
)
public class EpidemicConfiguration {

  @WatchPhase(forOrder = 1)
  private BaseModel model;

  @EndCondition(forOrder = 1)
  public boolean firstPhaseEndCondition() {
    return model.time() >= 3.0;
  }

}
