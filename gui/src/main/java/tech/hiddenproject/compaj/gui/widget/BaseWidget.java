package tech.hiddenproject.compaj.gui.widget;

import java.util.function.Consumer;

import javafx.scene.Node;

public class BaseWidget implements WorkSpaceWidget {

  private Node root;
  private String title;
  private Consumer<WorkSpaceWidget> onChildAddedEvent = c -> {
  };
  private Consumer<WorkSpaceWidget> onShow = c -> {
  };

  public BaseWidget(Node root) {
    this.root = root;
    this.title = "BaseWidget { root = " + root.toString() + " }";
  }

  public BaseWidget(Node root, String title) {
    this.root = root;
    this.title = title;
  }

  @Override
  public void onChildAdded(Consumer<WorkSpaceWidget> event) {
    onChildAddedEvent = event;
  }

  @Override
  public Node getNode() {
    return root;
  }

  @Override
  public void addChild(WorkSpaceWidget widget) {
    onChildAddedEvent.accept(widget);
  }

  @Override
  public void show() {
    onShow.accept(this);
  }

  @Override
  public void onShow(Consumer<WorkSpaceWidget> event) {
    onShow = event;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return title;
  }
}
