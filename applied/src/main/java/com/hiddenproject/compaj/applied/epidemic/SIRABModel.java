package com.hiddenproject.compaj.applied.epidemic;

import java.util.function.*;
import com.hiddenproject.compaj.core.data.*;
import com.hiddenproject.compaj.core.data.base.*;
import com.hiddenproject.compaj.core.model.base.*;

public class SIRABModel {

  private GridABM gridABM;

  public SIRABModel() {
    gridABM = new GridABM(300, 300);
  }

  public GridABM model() {
    return gridABM;
  }

  public void addObject(EnvironmentObject<GridLocation> obj) {
    gridABM.addEnvironmentObject(obj);
  }

  public AgentBuilder addSusceptible() {
    AgentBuilder agentBuilder = new AgentBuilder("S");
    return agentBuilder;
  }

  public AgentBuilder addInfected() {
    AgentBuilder agentBuilder = new AgentBuilder("I")
        .withDoubleProperty("infectStart", 0d);
    return agentBuilder;
  }

  public AgentBuilder addRecovered() {
    AgentBuilder agentBuilder = new AgentBuilder("R");
    return agentBuilder;
  }

  public class AgentBuilder {

    private GridAgent agent;
    private Double alpha;
    private Double minInfectTime;
    private GridLocation startLocation;

    public AgentBuilder(String s) {
      agent = new GridAgent(s);
      startLocation = new GridLocation(0, 0);
      agent.setLocation(startLocation);
      agent.moveAs((i, it) -> {
        double x = it.getLocation().getX() + ((Math.random() - 0.5) * 10);
        double y = it.getLocation().getY() + ((Math.random() - 0.5) * 10);
        return new GridLocation(x, y);
      });
      agent.stepAs((i, it) -> {
        if (Math.random() < alpha && it.getGroup().equals("I") && i
            - it.getDoubleProperty("infectStart") > minInfectTime) {
          it.setGroup("R");
        }
      });
      agent.interactsAs((it, b) -> {
        b.setGroup("I");
        b.setProperty("infectStart", gridABM.iterator());
      });
      agent.connectsIf((it, b) -> it.getGroup().equals("I") && Math.random() < 0.4 && b.getGroup().equals("S"));
    }

    public AgentBuilder withMaxDistance(Double dist) {
      agent.nearIf((it, b) -> it.getLocation().dist(b.getLocation()) <= dist);
      return this;
    }

    public AgentBuilder withBetta(Double value) {
      agent.connectsIf((it, b) -> it.getGroup().equals("I") && Math.random() < value && b.getGroup().equals("S"));
      return this;
    }

    public AgentBuilder withAlpha(Double value) {
      alpha = value;
      return this;
    }

    public AgentBuilder withMinInfectTime(Double value) {
      minInfectTime = value;
      return this;
    }

    public AgentBuilder withStartLoc(double x, double y) {
      startLocation.setX(x);
      startLocation.setY(y);
      return this;
    }

    public AgentBuilder withStartLoc(Supplier<GridLocation> func) {
      agent.setLocation(func);
      return this;
    }

    private AgentBuilder withDoubleProperty(String name, Double value) {
      agent.setProperty(name, value);
      return this;
    }

    public void count(int count) {
      gridABM.addAgent(agent, count);
    }

  }

}
