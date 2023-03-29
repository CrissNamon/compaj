package tech.hiddenproject.compaj.gui.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import tech.hiddenproject.compaj.gui.util.I18nUtils;

public class AlertBuilder {

  private final Alert alert;
  private static final String ALERT_CONFIRM = I18nUtils.get("alert.confirm");
  private static final String ALERT_UNSAVED_HEADER = I18nUtils.get("alert.unsaved.header");
  private static final String ALERT_UNSAVED_CONTENT = I18nUtils.get("alert.unsaved.content");
  private static final String ALERT_CANCEL = I18nUtils.get("alert.cancel");
  private static final String ALERT_OK = I18nUtils.get("alert.ok");

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
      new AlertBuilder(ALERT_CONFIRM, Alert.AlertType.CONFIRMATION)
      .header(ALERT_UNSAVED_HEADER)
      .content(ALERT_UNSAVED_CONTENT)
      .clearButtonTypes()
      .button(ALERT_CANCEL, ButtonBar.ButtonData.CANCEL_CLOSE)
      .button(ALERT_OK, ButtonBar.ButtonData.OK_DONE)
      .build();
  }
}
