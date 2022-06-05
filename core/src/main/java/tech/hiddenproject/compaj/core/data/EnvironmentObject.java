package tech.hiddenproject.compaj.core.data;

import java.util.List;

public interface EnvironmentObject<L extends Distance<L>> {

  EnvironmentObject<L> addPoint(L point);

  boolean collides(L point);

  List<L> getPoints();
}
