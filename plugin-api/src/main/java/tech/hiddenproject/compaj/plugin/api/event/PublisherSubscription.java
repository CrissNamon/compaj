package tech.hiddenproject.compaj.plugin.api.event;

import java.util.Objects;

/**
 * Represents subscription on {@link EventPublisher}.
 */
public final class PublisherSubscription {

  private final String topic;
  private final Long id;


  /**
   * @param topic Topic name
   * @param id    Subscription id
   */
  public PublisherSubscription(String topic, Long id) {
    this.topic = topic;
    this.id = id;
  }

  public String topic() {
    return topic;
  }

  public Long id() {
    return id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    PublisherSubscription publisherSubscription = (PublisherSubscription) obj;
    return topic().equals(publisherSubscription.topic())
        && id().equals(publisherSubscription.id());
  }

  @Override
  public String toString() {
    return "PublisherSubscription[" +
        "topic=" + topic + ", " +
        "id=" + id + ']';
  }


}
