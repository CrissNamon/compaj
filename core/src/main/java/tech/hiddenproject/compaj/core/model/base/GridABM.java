package tech.hiddenproject.compaj.core.model.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import tech.hiddenproject.compaj.core.data.EnvironmentObject;
import tech.hiddenproject.compaj.core.data.base.GridAgent;
import tech.hiddenproject.compaj.core.data.base.GridLocation;
import tech.hiddenproject.compaj.core.model.AgentModel;

public class GridABM implements AgentModel<GridAgent, GridLocation> {

  private final int width;
  private final int height;
  private final Random rnd;
  private List<List<GridAgent>> history;
  private List<GridAgent> agents;
  private List<EnvironmentObject<GridLocation>> environmentObjects;
  private long idGenerator = 0;
  private long iterator = 0;
  private boolean saveHistory;

  public GridABM(int width, int height, boolean saveHistory) {
    this(width, height);
    this.saveHistory = saveHistory;
  }

  public GridABM(int width, int height) {
    agents = new ArrayList<>();
    history = new ArrayList<>();
    environmentObjects = new ArrayList<>();
    this.width = width;
    this.height = height;
    rnd = new Random();
    saveHistory = true;
  }

  @Override
  public void addAgent(GridAgent agent, int count) {
    while (count-- > 0) {
      GridAgent clone = agent.clone();
      clone.setId(++idGenerator);
      agents.add(clone);
    }
  }

  @Override
  public void addEnvironmentObject(EnvironmentObject<GridLocation> environmentObject) {
    environmentObjects.add(environmentObject);
  }

  @Override
  public List<EnvironmentObject<GridLocation>> environmentObjects() {
    return environmentObjects;
  }

  @Override
  public void start(Function<Long, Boolean> endCondition) {
    Collections.shuffle(agents);
    while (endCondition.apply(iterator)) {
      step();
    }
  }

  @Override
  public void step() {
    for (GridAgent a : agents) {
      interactStep(a);
      GridLocation location = a.move(iterator);
      for (EnvironmentObject<GridLocation> object : environmentObjects) {
        if (object.collides(location)) {
          location = a.collides(object, location);
          break;
        }
      }
      a.setLocation(location);
      a.step(iterator);
    }
    if (saveHistory) {
      history.add(copyAgents());
    }
    iterator++;
  }

  @Override
  public long iterator() {
    return iterator;
  }

  @Override
  public List<List<GridAgent>> getHistory() {
    return history;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  private void interactStep(GridAgent a) {
    for (GridAgent b : agents) {
      if (a.getId() == b.getId()) {
        continue;
      }
      if (a.isNear(b) && a.connects(b)) {
        a.interacts(b);
      }
    }
  }

  private List<GridAgent> copyAgents() {
    List<GridAgent> state = new ArrayList<>();
    for (GridAgent a : agents) {
      state.add(a.clone());
    }
    return state;
  }
}
