package com.hiddenproject.compaj.gui;

import java.io.*;
import com.hiddenproject.compaj.gui.tab.*;
import com.hiddenproject.compaj.gui.util.*;
import com.hiddenproject.compaj.gui.widget.*;
import com.hiddenproject.compaj.lang.*;
import com.hiddenproject.compaj.lang.groovy.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import jfxtras.styles.jmetro.*;

public class Compaj extends Application {

  private static final GroovyTranslator translator;

  private static TabPane content;
  private static TerminalTab terminalTab;
  private static WorkSpaceTab workSpaceTab;
  private static Stage mainStage;

  static {
    GroovyTranslator.getImportCustomizer()
        .addStarImports(
            "com.hiddenproject.compaj.gui",
            "com.hiddenproject.compaj.gui.widget",
            "com.hiddenproject.compaj.gui.component"
        );
  }

  static {
    TranslatorUtils translatorUtils = new GroovyTranslatorUtils();
    translator = new GroovyTranslator(translatorUtils);
  }

  public static void main(String[] args) {
    launch(args);
  }

  public static void addWorkSpaceWidget(String name, Node node) {
    addWorkSpaceWidget(new BaseWidget(node, name));
  }

  public static void addWorkSpaceWidget(Node node) {
    addWorkSpaceWidget(new BaseWidget(node));
  }

  public static Object exec(String path) {
    File f = new File(path);
    return getTranslator().evaluate(
        FileUtils.readFile(f)
    );
  }

  public static GroovyTranslator getTranslator() {
    return translator;
  }

  public static Object exec() {
    File f = FileUtils.openFileWindow();
    return getTranslator().evaluate(
        FileUtils.readFile(f)
    );
  }

  public static Stage getMainStage() {
    return mainStage;
  }

  @Override
  public void start(Stage stage) {
    mainStage = stage;
    content = new TabPane();
    terminalTab = new TerminalTab();
    workSpaceTab = new WorkSpaceTab();
    stage.setTitle("CompaJ");
    BorderPane rootNode = new BorderPane();
    MenuBar menuBar = new MenuBar();
    final String os = System.getProperty("os.name");
    if (os != null && os.startsWith("Mac")) {
      menuBar.useSystemMenuBarProperty().set(true);
    }
    Menu mainMenu = new Menu("Вид");
    Menu helpMenu = new Menu("Помощь");
    Menu modelsMenu = new Menu("Простые модели");
    Menu infectModels = new Menu("Эпидемиология");
    MenuItem sirModel = new MenuItem("SIR");
    sirModel.setOnAction(actionEvent -> {
      addWorkSpaceWidget(new SIRModelWidget());
      content.getSelectionModel().select(workSpaceTab);
    });
    MenuItem sisModel = new MenuItem("SIS");
    sisModel.setOnAction(actionEvent -> {
      addWorkSpaceWidget(new SISModelWidget());
      content.getSelectionModel().select(workSpaceTab);
    });
    MenuItem seirModel = new MenuItem("SEIR");
    infectModels.getItems().addAll(sirModel, sisModel, seirModel);
    modelsMenu.getItems().add(infectModels);
    MenuItem terminalItem = new MenuItem("Терминал");
    MenuItem workSpaceItem = new MenuItem("Рабочее пространство");
    MenuItem editorItem = new MenuItem("Редактор");
    mainMenu.getItems().addAll(terminalItem, editorItem, workSpaceItem);
    menuBar.getMenus().addAll(mainMenu, modelsMenu, helpMenu);
    terminalItem.setOnAction(actionEvent -> content.getTabs().add(terminalTab));
    workSpaceItem.setOnAction(actionEvent -> content.getTabs().add(workSpaceTab));
    editorItem.setOnAction(actionEvent -> content.getTabs().add(new EditorTab()));
    content.getTabs().add(terminalTab);
    rootNode.setTop(menuBar);
    rootNode.setCenter(content);
    Scene scene = new Scene(rootNode, 1280, 720);
    scene.getStylesheets().add(getClass().getResource("/java-keywords.css").toExternalForm());
    new JMetro(Style.LIGHT).setScene(scene);
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    content.getTabs().forEach(t -> {
      EventHandler closeEvent = t.getOnClosed();
      if (closeEvent != null) {
        closeEvent.handle(null);
      }
    });
    content.getTabs().clear();
  }

  public static void addWorkSpaceWidget(WorkSpaceWidget workSpaceWidget) {
    workSpaceTab.addItem(workSpaceWidget);
    if (! content.getTabs().contains(workSpaceTab)) {
      content.getTabs().add(workSpaceTab);
    }
  }

}