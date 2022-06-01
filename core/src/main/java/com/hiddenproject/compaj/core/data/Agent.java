package com.hiddenproject.compaj.core.data;

import java.util.function.*;

public interface Agent<L extends Distance<L>, G> {

  L getLocation();

  void setLocation(L location);

  boolean isNear(Agent<L, ?> a);

  boolean connects(Agent<L, G> a);

  void interacts(Agent<L, G> a);

  G getGroup();

  void setGroup(G group);

  void step(long i);

  L move(long i);

  L collides(EnvironmentObject<L> with, L at);

  Object getId();

  void setId(Object id);

  void connectsIf(BiFunction<Agent<L, G>, Agent<L, G>, Boolean> function);

  void interactsAs(BiConsumer<Agent<L, G>, Agent<L, G>> function);

  void nearIf(BiFunction<Agent<L, ?>, Agent<L, ?>, Boolean> function);

  void moveAs(BiFunction<Long, Agent<L, ?>, L> function);

  void collidesAs(BiFunction<EnvironmentObject<L>, L, L> function);

  void stepAs(BiConsumer<Long, Agent<L, G>> function);

  void setProperty(String key, Number value);

  void setProperty(String key, String value);

  double getDoubleProperty(String key);

  String getStringProperty(String key);
}
