package tech.hiddenproject.compaj.gui.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import tech.hiddenproject.compaj.gui.util.I18nUtils;

/**
 * Builder for alerts.
 */
public class AlertBuilder {

  private static final String ALERT_CONFIRM = "alert.confirm";
  private static final String ALERT_UNSAVED_HEADER = "alert.unsaved.header";
  private static final String ALERT_UNSAVED_CONTENT = "alert.unsaved.content";
  private static final String ALERT_CANCEL = "alert.cancel";
  private static final String ALERT_OK = "alert.ok";

  private final Alert alert;

  /**
   * Creates new builder.
   *
   * @param title Alert title
   */
  public AlertBuilder(String title) {
    this(title, Alert.AlertType.NONE);
    alert.getButtonTypes().clear();
  }

  /**
   * Creates new builder.
   *
   * @param title     Alert title
   * @param alertType {@link javafx.scene.control.Alert.AlertType}
   */
  public AlertBuilder(String title, Alert.AlertType alertType) {
    alert = new Alert(alertType);
    alert.setTitle(title);
  }

  /**
   * Clears buttons.
   *
   * @return {@link AlertBuilder}
   */
  public AlertBuilder clearButtonTypes() {
    alert.getButtonTypes().clear();
    return this;
  }

  /**
   * Sets alert header.
   *
   * @param text Alert header
   * @return {@link AlertBuilder}
   */
  public AlertBuilder header(String text) {
    alert.setHeaderText(text);
    return this;
  }

  /**
   * Sets alert content.
   *
   * @param text Alert content
   * @return {@link AlertBuilder}
   */
  public AlertBuilder content(String text) {
    alert.setContentText(text);
    return this;
  }

  /**
   * Adds button to alert.
   *
   * @param name       Button name
   * @param buttonData {@link javafx.scene.control.ButtonBar.ButtonData}.
   * @return {@link AlertBuilder}
   */
  public AlertBuilder button(String name, ButtonBar.ButtonData buttonData) {
    ButtonType buttonType = new ButtonType(name, buttonData);
    alert.getButtonTypes().add(0, buttonType);
    return this;
  }

  /**
   * Creates {@link Alert}.
   *
   * @return {@link Alert}
   */
  public Alert build() {
    return alert;
  }

  /**
   * Contains prebuilt alerts.
   */
  public static class Prebuilt {

    /**
     * {@link Alert} to confirm close.
     */
    public static final Alert CLOSE_CONFIRMATION =
        new AlertBuilder(I18nUtils.get(ALERT_CONFIRM), Alert.AlertType.CONFIRMATION)
            .header(I18nUtils.get(ALERT_UNSAVED_HEADER))
            .content(I18nUtils.get(ALERT_UNSAVED_CONTENT))
            .clearButtonTypes()
            .button(I18nUtils.get(ALERT_CANCEL), ButtonBar.ButtonData.CANCEL_CLOSE)
            .button(I18nUtils.get(ALERT_OK), ButtonBar.ButtonData.OK_DONE)
            .build();

  }
}
