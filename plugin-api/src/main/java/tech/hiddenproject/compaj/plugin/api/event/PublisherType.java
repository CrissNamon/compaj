package tech.hiddenproject.compaj.plugin.api.event;

/**
 * Defines how events will be sent to listeners.
 */
public enum PublisherType {
  /**
   * All listeners will be notified in sequence they were subscribed.
   */
  SEQUENCE,

  /**
   * Try to notify listeners using parallel unordered streams.
   */
  PARALLEL
}