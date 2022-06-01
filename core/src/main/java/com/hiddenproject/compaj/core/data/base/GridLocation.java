package com.hiddenproject.compaj.core.data.base;

import com.hiddenproject.compaj.core.data.*;

public class GridLocation implements Distance<GridLocation> {

  private double x;
  private double y;

  public GridLocation(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public double dist(GridLocation o2) {
    return Math.sqrt(Math.pow(getX() - o2.getX(), 2) + Math.pow(getY() - o2.getY(), 2));
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  @Override
  public String toString() {
    return "GridLocation{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
}
