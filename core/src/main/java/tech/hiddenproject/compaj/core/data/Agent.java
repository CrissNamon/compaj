package tech.hiddenproject.compaj.core.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Represents agent for {@link tech.hiddenproject.compaj.core.model.AgentModel}.
 *
 * @param <L> Location type
 * @param <G> Group type
 */
public interface Agent<L extends Distance<L>, G> {

  /**
   * @return Current location
   */
  L getLocation();

  /**
   * @param location New location
   */
  void setLocation(L location);

  /**
   * @param a {@link Agent}
   * @return true if given agent is near current agent
   */
  boolean isNear(Agent<L, ?> a);

  /**
   * @param a {@link Agent}
   * @return true if current agent should connect given agent
   */
  boolean connects(Agent<L, G> a);

  /**
   * Interact with given agent.
   *
   * @param a {@link Agent}
   */
  void interacts(Agent<L, G> a);

  /**
   * @return Agent group
   */
  G getGroup();

  /**
   * @param group New group
   */
  void setGroup(G group);

  /**
   * @param i Step distance
   */
  void step(long i);

  /**
   * @param i Move distance
   * @return New location
   */
  L move(long i);

  /**
   * @param with {@link EnvironmentObject}
   * @param at   Location to check
   * @return Location
   */
  L collides(EnvironmentObject<L> with, L at);

  /**
   * @return Agent id
   */
  Object getId();

  /**
   * @param id New id
   */
  void setId(Object id);

  /**
   * Defines condition on when two agent should connect.
   *
   * @param function Connection function
   */
  void connectsIf(BiFunction<Agent<L, G>, Agent<L, G>, Boolean> function);

  /**
   * Defines how two agent should interact.
   *
   * @param function Interaction function
   */
  void interactsAs(BiConsumer<Agent<L, G>, Agent<L, G>> function);

  /**
   * Defines condition on when two agent are near.
   *
   * @param function Distance check function
   */
  void nearIf(BiFunction<Agent<L, ?>, Agent<L, ?>, Boolean> function);

  /**
   * Defines how agent should move.
   *
   * @param function Movement function
   */
  void moveAs(BiFunction<Long, Agent<L, ?>, L> function);

  /**
   * Defines how agent should collide {@link EnvironmentObject}.
   *
   * @param function Collider function
   */
  void collidesAs(BiFunction<EnvironmentObject<L>, L, L> function);

  /**
   * Defines how agent does a step.
   *
   * @param function Step function
   */
  void stepAs(BiConsumer<Long, Agent<L, G>> function);

  /**
   * @param key   Property name
   * @param value Property value
   */
  void setProperty(String key, Number value);

  /**
   * @param key   Property name
   * @param value Property value
   */
  void setProperty(String key, String value);

  /**
   * @param key Property name
   * @return Property value
   */
  double getDoubleProperty(String key);

  /**
   * @param key Property name
   * @return Property value
   */
  String getStringProperty(String key);
}
