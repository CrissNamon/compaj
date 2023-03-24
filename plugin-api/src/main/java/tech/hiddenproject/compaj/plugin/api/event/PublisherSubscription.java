package tech.hiddenproject.compaj.plugin.api.event;

/**
 * Represents subscription on {@link EventPublisher}.
 */
public record PublisherSubscription(String topic, Long id) {

  /**
   * @param topic Topic name
   * @param id    Subscription id
   */
  public PublisherSubscription {
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
