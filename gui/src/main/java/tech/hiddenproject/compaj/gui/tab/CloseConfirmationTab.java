package tech.hiddenproject.compaj.gui.tab;

import java.util.Optional;

import javafx.event.Event;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import tech.hiddenproject.compaj.gui.component.AlertBuilder.Prebuilt;

public abstract class CloseConfirmationTab extends Tab {

  public CloseConfirmationTab(String name) {
    super(name);
    setOnCloseRequest(this::onCloseRequest);
  }

  private void onCloseRequest(Event event) {
    Optional<ButtonType> result = Prebuilt.CLOSE_CONFIRMATION.showAndWait();
    boolean choice = result.map(ButtonType::getButtonData)
        .map(buttonData -> buttonData != ButtonBar.ButtonData.OK_DONE)
        .orElse(false);
    if (choice) {
      event.consume();
    }
  }
}
