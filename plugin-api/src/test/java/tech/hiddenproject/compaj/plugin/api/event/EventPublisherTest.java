package tech.hiddenproject.compaj.plugin.api.event;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class EventPublisherTest {

  private static final String EVENT_NAME = "EVENT";
  private static final String EVENT_PAYLOAD = "EVENT_PAYLOAD";
  private static final String TOPIC = "TOPIC";

  @Test
  public void subscribeConsumerTest() {
    Consumer<CompaJEvent> compaJEventConsumer = Mockito.mock(Consumer.class);
    CompaJEvent compaJEvent = new CompaJEvent(EVENT_NAME, EVENT_PAYLOAD);
    Mockito.doNothing().when(compaJEventConsumer).accept(compaJEvent);
    EventPublisher.INSTANCE.subscribeOn(TOPIC, compaJEventConsumer);
    EventPublisher.INSTANCE.sendTo(TOPIC, compaJEvent);

    Mockito.verify(compaJEventConsumer, Mockito.times(1)).accept(compaJEvent);
    Mockito.verifyNoMoreInteractions(compaJEventConsumer);
  }

  @Test
  public void unsubscribeTest() {
    Consumer<CompaJEvent> compaJEventConsumer = Mockito.mock(Consumer.class);
    CompaJEvent compaJEvent = new CompaJEvent(EVENT_NAME, EVENT_PAYLOAD);
    Mockito.doNothing().when(compaJEventConsumer).accept(compaJEvent);

    PublisherSubscription sub = EventPublisher.INSTANCE.subscribeOn(TOPIC, compaJEventConsumer);
    EventPublisher.INSTANCE.sendTo(TOPIC, compaJEvent);

    EventPublisher.INSTANCE.unsubscribe(sub);
    EventPublisher.INSTANCE.sendTo(TOPIC, compaJEvent);

    Mockito.verify(compaJEventConsumer, Mockito.times(1)).accept(compaJEvent);
    Mockito.verifyNoMoreInteractions(compaJEventConsumer);
  }

}
