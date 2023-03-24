package tech.hiddenproject.compaj.core.data;

import java.util.List;

/**
 * Represents environment object for {@link tech.hiddenproject.compaj.core.model.AgentModel}.
 * Environment object is a polygon constructed from points.
 *
 * @param <L> Location type, implementation of {@link Distance}
 */
public interface EnvironmentObject<L extends Distance<L>> {

  /**
   * Add new point to object.
   *
   * @param point {@link Distance}
   * @return This {@link EnvironmentObject}
   */
  EnvironmentObject<L> addPoint(L point);

  /**
   * Checks if this polygon collides with given point.
   *
   * @param point {@link Distance}
   * @return true if this polygon collides with given point
   */
  boolean collides(L point);

  /**
   * @return {@link List} of all {@link Distance} points
   */
  List<L> getPoints();
}
