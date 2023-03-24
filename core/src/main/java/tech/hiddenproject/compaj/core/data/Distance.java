package tech.hiddenproject.compaj.core.data;

/**
 * Represents distance calculator between two points.
 *
 * @param <T> Point type
 */
public interface Distance<T> {

  /**
   * @param o2 Point
   * @return Distance between points
   */
  double dist(T o2);
}
