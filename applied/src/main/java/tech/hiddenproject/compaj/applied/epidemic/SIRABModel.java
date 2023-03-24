package tech.hiddenproject.compaj.applied.epidemic;

import java.util.function.Supplier;

import tech.hiddenproject.compaj.core.data.EnvironmentObject;
import tech.hiddenproject.compaj.core.data.base.GridAgent;
import tech.hiddenproject.compaj.core.data.base.GridLocation;
import tech.hiddenproject.compaj.core.model.base.GridABM;

/**
 * Epidemic agent based SIR model.
 */
public class SIRABModel {

  private final GridABM gridABM;

  public SIRABModel() {
    gridABM = new GridABM(300, 300);
  }

  public SIRABModel(int width, int height) {
    gridABM = new GridABM(width, height);
  }

  public GridABM model() {
    return gridABM;
  }

  public void addObject(EnvironmentObject<GridLocation> obj) {
    gridABM.addEnvironmentObject(obj);
  }

  public AgentBuilder addSusceptible() {
    return new AgentBuilder("S");
  }

  public AgentBuilder addInfected() {
    return new AgentBuilder("I").withDoubleProperty("infectStart", 0d);
  }

  public AgentBuilder addRecovered() {
    return new AgentBuilder("R");
  }

  public class AgentBuilder {

    private final GridAgent agent;
    private final GridLocation startLocation;
    private Double alpha;
    private Double minInfectTime;

    public AgentBuilder(String s) {
      agent = new GridAgent(s);
      startLocation = new GridLocation(0, 0);
      agent.setLocation(startLocation);
      agent.moveAs(
          (i, it) -> {
            double x = it.getLocation().getX() + ((Math.random() - 0.5) * 10);
            double y = it.getLocation().getY() + ((Math.random() - 0.5) * 10);
            return new GridLocation(x, y);
          });
      agent.stepAs(
          (i, it) -> {
            if (Math.random() < alpha
                && it.getGroup().equals("I")
                && i - it.getDoubleProperty("infectStart") > minInfectTime) {
              it.setGroup("R");
            }
          });
      agent.interactsAs(
          (it, b) -> {
            b.setGroup("I");
            b.setProperty("infectStart", gridABM.iterator());
          });
      agent.connectsIf(
          (it, b) -> it.getGroup().equals("I") && Math.random() < 0.4 && b.getGroup().equals("S"));
    }

    public AgentBuilder withMaxDistance(Double dist) {
      agent.nearIf((it, b) -> it.getLocation().dist(b.getLocation()) <= dist);
      return this;
    }

    public AgentBuilder withBetta(Double value) {
      agent.connectsIf(
          (it, b) ->
              it.getGroup().equals("I") && Math.random() < value && b.getGroup().equals("S"));
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
