package com.hiddenproject.compaj.core.data.base;

import java.util.*;
import java.util.function.*;
import com.hiddenproject.compaj.core.data.*;

public class GridAgent implements Agent<GridLocation, String> {

  private Object id;
  private GridLocation location;
  private Supplier<GridLocation> locationSupplier;
  private String group;
  private BiFunction<Agent<GridLocation, String>, Agent<GridLocation, String>, Boolean> connectFunction;
  private BiConsumer<Agent<GridLocation, String>, Agent<GridLocation, String>> interactFunction;
  private BiFunction<Agent<GridLocation, ?>, Agent<GridLocation, ?>, Boolean> nearFunction;
  private BiFunction<Long, Agent<GridLocation, ?>, GridLocation> moveFunction;
  private BiFunction<EnvironmentObject<GridLocation>, GridLocation, GridLocation> collideFunction;
  private BiConsumer<Long, Agent<GridLocation, String>> stepFunction;
  private Map<String, Double> numericProperties;
  private Map<String, String> stringProperties;

  public GridAgent(String group) {
    this.id = 1;
    this.group = group;
    this.numericProperties = new HashMap<>();
    this.stringProperties = new HashMap<>();
    connectsIf((a, b) -> true);
    interactsAs((a, b) -> {
    });
    nearIf((a, b) -> getLocation().dist(b.getLocation()) <= 2);
    moveAs((i, a) -> {
      double x = getLocation().getX() + ((Math.random() - 0.5) * 20) * Math.cos(Math.random() * 360);
      double y = getLocation().getY() + ((Math.random() - 0.5) * 20) * Math.sin(Math.random() * 360);
      return new GridLocation(x, y);
    });
    collidesAs((with, at) -> {
      double min = with.getPoints().get(0).dist(at);
      int pointNum = 0;
      for (int i = 1; i < with.getPoints().size(); i++) {
        GridLocation point = with.getPoints().get(i);
        double dist = point.dist(at);
        if (dist < min) {
          min = dist;
          pointNum = i;
        }
      }
      return with.getPoints().get(pointNum);
    });
    stepAs((i, a) -> {
    });
    setLocation(new GridLocation(0, 0));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }  @Override
  public void setLocation(GridLocation location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GridAgent gridAgent = (GridAgent) o;
    return id.equals(gridAgent.id);
  }  public void setLocation(Supplier<GridLocation> func) {
    locationSupplier = func;
  }

  @Override
  public GridAgent clone() {
    GridAgent newAgent = new GridAgent(getGroup());
    newAgent.setId(getId());
    if (locationSupplier == null) {
      newAgent.setLocation(new GridLocation(getLocation().getX(), getLocation().getY()));
    } else {
      newAgent.setLocation(locationSupplier.get());
    }
    newAgent.interactsAs(interactFunction);
    newAgent.connectsIf(connectFunction);
    newAgent.nearIf(nearFunction);
    newAgent.stepAs(stepFunction);
    newAgent.moveAs(moveFunction);
    newAgent.collidesAs(collideFunction);
    for (Map.Entry<String, Double> entry : numericProperties.entrySet()) {
      newAgent.setProperty(entry.getKey(), new Double(entry.getValue()));
    }
    for (Map.Entry<String, String> entry : stringProperties.entrySet()) {
      newAgent.setProperty(entry.getKey(), new String(entry.getValue()));
    }
    return newAgent;
  }  @Override
  public GridLocation getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return "GridAgent{" +
        "id=" + id +
        ", location=" + location +
        ", group='" + group + '\'' +
        '}';
  }  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public void setGroup(String group) {
    this.group = group;
  }

  @Override
  public void step(long i) {
    stepFunction.accept(i, this);
  }

  @Override
  public GridLocation move(long i) {
    return moveFunction.apply(i, this);
  }

  @Override
  public GridLocation collides(EnvironmentObject<GridLocation> with, GridLocation at) {
    return collideFunction.apply(with, at);
  }



  @Override
  public void setId(Object id) {
    this.id = id;
  }

  @Override
  public Object getId() {
    return id;
  }

  @Override
  public void connectsIf(BiFunction<Agent<GridLocation, String>, Agent<GridLocation, String>, Boolean> function) {
    connectFunction = function;
  }

  @Override
  public void interactsAs(BiConsumer<Agent<GridLocation, String>, Agent<GridLocation, String>> function) {
    interactFunction = function;
  }

  @Override
  public void nearIf(BiFunction<Agent<GridLocation, ?>, Agent<GridLocation, ?>, Boolean> function) {
    nearFunction = function;
  }

  @Override
  public void moveAs(BiFunction<Long, Agent<GridLocation, ?>, GridLocation> function) {
    moveFunction = function;
  }

  @Override
  public void collidesAs(BiFunction<EnvironmentObject<GridLocation>, GridLocation, GridLocation> function) {
    collideFunction = function;
  }

  @Override
  public void stepAs(BiConsumer<Long, Agent<GridLocation, String>> function) {
    stepFunction = function;
  }

  @Override
  public void setProperty(String key, Number value) {
    numericProperties.put(key, value.doubleValue());
  }

  @Override
  public void setProperty(String key, String value) {
    stringProperties.put(key, value);
  }

  @Override
  public double getDoubleProperty(String key) {
    return numericProperties.get(key);
  }

  @Override
  public String getStringProperty(String key) {
    return stringProperties.get(key);
  }

  @Override
  public boolean isNear(Agent<GridLocation, ?> a) {
    return nearFunction.apply(this, a);
  }

  @Override
  public boolean connects(Agent<GridLocation, String> a) {
    return connectFunction.apply(this, a);
  }

  @Override
  public void interacts(Agent<GridLocation, String> a) {
    interactFunction.accept(this, a);
  }






}
