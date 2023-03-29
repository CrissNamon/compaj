package tech.hiddenproject.compaj.gui.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import tech.hiddenproject.compaj.gui.util.I18nUtils;

public class AlertBuilder {

  private final Alert alert;

  public AlertBuilder(String title) {
    this(title, Alert.AlertType.NONE);
    alert.getButtonTypes().clear();
  }

  public AlertBuilder(String title, Alert.AlertType alertType) {
    alert = new Alert(alertType);
    alert.setTitle(title);
  }

  public AlertBuilder clearButtonTypes() {
    alert.getButtonTypes().clear();
    return this;
  }

  public AlertBuilder header(String text) {
    alert.setHeaderText(text);
    return this;
  }

  public AlertBuilder content(String text) {
    alert.setContentText(text);
    return this;
  }

  public AlertBuilder button(String name, ButtonBar.ButtonData buttonData) {
    ButtonType buttonType = new ButtonType(name, buttonData);
    alert.getButtonTypes().add(0, buttonType);
    return this;
  }

  public Alert build() {
    return alert;
  }

  public static class Prebuilt {

    public static final Alert CLOSE_CONFIRMATION =
        new AlertBuilder(I18nUtils.get("alert.confirm"), Alert.AlertType.CONFIRMATION)
            .header(I18nUtils.get("alert.unsaved.header"))
            .content(I18nUtils.get("alert.unsaved.content"))
            .clearButtonTypes()
            .button(I18nUtils.get("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE)
            .button(I18nUtils.get("alert.ok"), ButtonBar.ButtonData.OK_DONE)
            .build();

  }
}
