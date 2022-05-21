package com.hiddenproject.compaj.gui;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        VBox inputs = new VBox();
        ScrollPane left = new ScrollPane(inputs);
        inputs.getChildren().add(new Label("REPL HERE"));
        left.vvalueProperty().bind(inputs.heightProperty());
        left.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox right = new VBox(new Label("DATA INSPECTOR HERE"));
        SplitPane root = new SplitPane();
        root.getItems().addAll(left, right);
        var scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}