package com.hiddenproject.compaj.example;

import com.hiddenproject.compaj.annotation.EndCondition;
import com.hiddenproject.compaj.annotation.Epidemic;
import com.hiddenproject.compaj.annotation.Phase;
import com.hiddenproject.compaj.annotation.WatchPhase;
import com.hiddenproject.compaj.core.model.base.BaseModel;

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
    return true;
  }

}
