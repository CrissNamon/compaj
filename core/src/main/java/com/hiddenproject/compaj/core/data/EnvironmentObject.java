package com.hiddenproject.compaj.core.data;

import java.util.*;

public interface EnvironmentObject<L extends Distance<L>> {

  EnvironmentObject<L> addPoint(L point);

  boolean collides(L point);

  List<L> getPoints();

}
