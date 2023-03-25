package tech.hiddenproject.compaj.plugin.api.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allow you to subscribe on and send global events.
 */
public enum EventPublisher {

  INSTANCE;

  private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisher.class);
  private final AtomicLong generator;
  private final Map<String, Map<Long, Consumer<CompaJEvent>>> feed;
  private PublisherType publisherType;

  EventPublisher() {
    feed = new ConcurrentHashMap<>();
    publisherType = PublisherType.PARALLEL;
    generator = new AtomicLong(0);
  }

  /**
   * Sends message to topic.
   *
   * @param topic   Topic name
   * @param message {@link CompaJEvent}
   */
  public void sendTo(String topic, CompaJEvent message) {
    Map<Long, Consumer<CompaJEvent>> subscribers = feed.getOrDefault(topic, new HashMap<>());
    LOGGER.info("Event {} on {}", message, topic);
    switch (publisherType) {
      case SEQUENCE:
        sendSequence(message, subscribers);
        break;
      case PARALLEL:
        sendParallel(message, subscribers);
        break;
    }
  }

  /**
   * Subscribes on topic with given {@link Consumer}.
   *
   * @param topic  Topic name
   * @param action {@link Consumer} for {@link CompaJEvent}
   * @return {@link PublisherSubscription}
   */
  public PublisherSubscription subscribeOn(String topic, Consumer<CompaJEvent> action) {
    feed.putIfAbsent(topic, new ConcurrentHashMap<>());
    long id = generator.incrementAndGet();
    feed.get(topic).put(id, action);
    return new PublisherSubscription(topic, id);
  }

  /**
   * @param subscription {@link PublisherSubscription}
   */
  public void unsubscribe(PublisherSubscription subscription) {
    feed.getOrDefault(subscription.topic(), new HashMap<>()).remove(subscription.id());
  }

  /**
   * @return Current {@link PublisherType}
   */
  PublisherType getPublisherType() {
    return publisherType;
  }

  /**
   * @param publisherType New {@link PublisherType}
   */
  void setPublisherType(PublisherType publisherType) {
    this.publisherType = publisherType;
  }

  private void sendSequence(CompaJEvent message, Map<Long, Consumer<CompaJEvent>> subscribers) {
    for (Consumer<CompaJEvent> compaJEventConsumer : subscribers.values()) {
      compaJEventConsumer.accept(message);
    }
  }

  private void sendParallel(CompaJEvent message, Map<Long, Consumer<CompaJEvent>> subscribers) {
    subscribers.values().parallelStream().unordered().forEach(l -> l.accept(message));
  }
}
