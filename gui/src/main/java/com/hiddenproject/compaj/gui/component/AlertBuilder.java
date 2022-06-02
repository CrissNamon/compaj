package com.hiddenproject.compaj.gui.component;

import javafx.scene.control.*;

public class AlertBuilder {

  private Alert alert;

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
}
