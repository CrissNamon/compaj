package com.hiddenproject.compaj.example;

import com.hiddenproject.compaj.framework.annotation.EndCondition;
import com.hiddenproject.compaj.framework.annotation.Phase;
import com.hiddenproject.compaj.framework.annotation.WatchPhase;
import com.hiddenproject.compaj.core.model.base.BaseModel;
import com.hiddenproject.compaj.framework.types.ModellingType;

@com.hiddenproject.compaj.framework.annotation.EpidemicConfiguration(
    name = "Test",
    phases = {
        @Phase(model = "SIR", order = 1),
        @Phase(model = "SEIR", order = 2)
    },
    type = ModellingType.ITERABLE
)
public class EpidemicConfiguration {

  @WatchPhase(forOrder = 1)
  private BaseModel model;

  @EndCondition(forOrder = 1)
  public boolean firstPhaseEndCondition() {
    return true;
  }

}
