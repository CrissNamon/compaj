package com.hiddenproject.compaj.core.data.base;

import java.util.*;
import com.hiddenproject.compaj.core.data.*;

public class PolygonObject implements EnvironmentObject<GridLocation> {

  private List<GridLocation> points;

  public PolygonObject() {
    points = new ArrayList<>();
  }

  public PolygonObject addPoint(GridLocation point) {
    points.add(point);
    return this;
  }

  @Override
  public boolean collides(GridLocation location) {
    boolean result = false;
    int j = points.size() - 1;
    for (int i = 0; i < points.size(); i++) {
      GridLocation pointI = points.get(i);
      GridLocation pointJ = points.get(j);
      if ((pointI.getY() < location.getY() && pointJ.getY() >= location.getY()
          || pointJ.getY() < location.getY() && pointI.getY() >= location.getY()) &&
          (pointI.getX() + (location.getY() - pointI.getY())
              / (pointJ.getY() - pointI.getY()) * (pointJ.getX() - pointI.getX()) < location.getX())) {
        result = ! result;
      }
      j = i;
    }
    return result;
  }

  @Override
  public List<GridLocation> getPoints() {
    return points;
  }
}
