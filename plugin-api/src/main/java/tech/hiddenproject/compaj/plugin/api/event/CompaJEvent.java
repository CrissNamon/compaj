package tech.hiddenproject.compaj.plugin.api.event;

import java.util.Objects;

/**
 * Represents event in CompaJ.
 */
public class CompaJEvent {

  private final String name;
  private final Object payload;

  public CompaJEvent(String name, Object payload) {
    Objects.requireNonNull(name);
    this.payload = payload;
    this.name = name;
  }

  /**
   * Casts and returns event's payload to given type.
   *
   * @param <T> Payload type
   * @return Cast payload
   */
  public <T> T getPayload() {
    return (T) payload;
  }

  /**
   * @return Event name
   */
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompaJEvent that = (CompaJEvent) o;
    return payload.equals(that.payload) && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(payload, name);
  }

  @Override
  public String toString() {
    return "CompaJEvent{" +
        "name='" + name + '\'' +
        ", payload=" + payload +
        '}';
  }

  /**
   * Global event names in CompaJ system.
   */
  public static class GLOBAL {
    public static final String STARTUP = "STARTUP";
  }
}
