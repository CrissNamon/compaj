package tech.hiddenproject.compaj.gui.event;

public class UiChildPayload {

  private final String rootId;
  private final Object node;

  public UiChildPayload(String rootId, Object node) {
    this.rootId = rootId;
    this.node = node;
  }

  public String getRootId() {
    return rootId;
  }

  public <N> N getNode() {
    return (N) node;
  }
}
