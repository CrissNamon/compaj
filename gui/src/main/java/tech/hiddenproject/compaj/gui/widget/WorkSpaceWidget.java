package tech.hiddenproject.compaj.gui.widget;

import java.util.function.Consumer;
import javafx.scene.Node;

public interface WorkSpaceWidget {

  void onChildAdded(Consumer<WorkSpaceWidget> event);

  Node getNode();

  default void addChild(WorkSpaceWidget widget) {}

  default void show() {}

  default void onShow(Consumer<WorkSpaceWidget> event) {}

  default void close() {}

  default void onClose(Consumer<WorkSpaceWidget> event) {}
}
