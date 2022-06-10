package tech.hiddenproject.compaj.gui;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.apache.commons.math3.complex.ComplexFormat;
import tech.hiddenproject.compaj.extension.AgentExtension;
import tech.hiddenproject.compaj.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.extension.ComplexExtension;
import tech.hiddenproject.compaj.extension.MathExtension;
import tech.hiddenproject.compaj.extension.ModelExtension;
import tech.hiddenproject.compaj.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.extension.StarterExtension;
import tech.hiddenproject.compaj.gui.tab.EditorTab;
import tech.hiddenproject.compaj.gui.tab.TerminalTab;
import tech.hiddenproject.compaj.gui.tab.WorkSpaceTab;
import tech.hiddenproject.compaj.gui.util.FileUtils;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.widget.BaseWidget;
import tech.hiddenproject.compaj.gui.widget.SIRModelWidget;
import tech.hiddenproject.compaj.gui.widget.SISModelWidget;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;
import tech.hiddenproject.compaj.translation.MathTranslations;

public class Compaj extends Application {

  public static final ComplexFormat COMPLEX_FORMAT;

  static {
    NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
    COMPLEX_FORMAT = new ComplexFormat(numberFormat, numberFormat);
  }

  private static final GroovyTranslator translator;

  private static TabPane content;
  private static TerminalTab terminalTab;
  private static WorkSpaceTab workSpaceTab;
  private static Stage mainStage;

  private static final String[] normalImports =
      new String[] {
          "tech.hiddenproject.compaj.extension.MathExtension",
          "tech.hiddenproject.compaj.extension.CompaJComplex"
      };

  private static final String[] starImports =
      new String[] {
          "tech.hiddenproject.compaj.gui",
          "tech.hiddenproject.compaj.gui.widget",
          "tech.hiddenproject.compaj.gui.component"
      };

  static {
    GroovyTranslator.getImportCustomizer()
        .addStarImports(starImports)
        .addImports(normalImports)
        .addStaticImport("tech.hiddenproject.compaj.gui.Compaj", "COMPLEX_FORMAT");
  }

  static {
    TranslatorUtils translatorUtils = new GroovyTranslatorUtils();
    translatorUtils.addCodeTranslation(MathTranslations::translateComplexNumbers);
    translatorUtils.addCodeTranslation(MathTranslations::translateMagicNumbers);
    translator = new GroovyTranslator(translatorUtils);
  }

  {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  public static void main(String[] args) {
    launch(args);
  }

  public static void addWorkSpaceWidget(String name, Node node) {
    addWorkSpaceWidget(new BaseWidget(node, name));
  }

  public static void addWorkSpaceWidget(WorkSpaceWidget workSpaceWidget) {
    workSpaceTab.addItem(workSpaceWidget);
    if (!content.getTabs().contains(workSpaceTab)) {
      content.getTabs().add(workSpaceTab);
    }
  }

  public static void addWorkSpaceWidget(Node node) {
    addWorkSpaceWidget(new BaseWidget(node));
  }

  public static Object exec(String path) {
    File f = new File(path);
    return getTranslator().evaluate(FileUtils.readFile(f));
  }

  public static GroovyTranslator getTranslator() {
    return translator;
  }

  public static Object exec() {
    File f = FileUtils.openNoteWindow();
    return getTranslator().evaluate(FileUtils.readFile(f));
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
    MenuItem sirModel = new MenuItem("SIR");
    sirModel.setOnAction(
        actionEvent -> {
          addWorkSpaceWidget(new SIRModelWidget());
          content.getSelectionModel().select(workSpaceTab);
        });
    MenuItem sisModel = new MenuItem("SIS");
    sisModel.setOnAction(
        actionEvent -> {
          addWorkSpaceWidget(new SISModelWidget());
          content.getSelectionModel().select(workSpaceTab);
        });
    MenuItem seirModel = new MenuItem("SEIR");
    Menu infectModels = new Menu(I18nUtils.get("menu.simple_models.epidemic"));
    infectModels.getItems().addAll(sirModel, sisModel, seirModel);
    Menu modelsMenu = new Menu(I18nUtils.get("menu.simple_models"));
    modelsMenu.getItems().add(infectModels);

    MenuItem terminalItem = new MenuItem(I18nUtils.get("tab.terminal.title"));
    terminalItem.setOnAction(actionEvent -> content.getTabs().add(terminalTab));
    MenuItem workSpaceItem = new MenuItem(I18nUtils.get("tab.workspace.title"));
    workSpaceItem.setOnAction(actionEvent -> content.getTabs().add(workSpaceTab));
    MenuItem editorItem = new MenuItem(I18nUtils.get("tab.editor.title"));
    editorItem.setOnAction(actionEvent -> content.getTabs().add(new EditorTab()));
    Menu mainMenu = new Menu(I18nUtils.get("menu.view"));
    mainMenu.getItems().addAll(terminalItem, editorItem, workSpaceItem);

    Menu helpMenu = new Menu(I18nUtils.get("menu.help"));

    MenuItem enLang = new MenuItem("English");
    enLang.setOnAction(actionEvent -> I18nUtils.changeLang(Locale.US));
    MenuItem ruLang = new MenuItem("Русский");
    ruLang.setOnAction(actionEvent -> I18nUtils.changeLang(Locale.forLanguageTag("ru-RU")));
    Menu languageMenu = new Menu(I18nUtils.get("menu.settings.lang"));
    languageMenu.getItems().addAll(enLang, ruLang);
    Menu settingsMenu = new Menu(I18nUtils.get("menu.settings"));
    settingsMenu.getItems().add(languageMenu);

    MenuBar menuBar = new MenuBar();
    final String os = System.getProperty("os.name");
    if (os != null && os.startsWith("Mac")) {
      menuBar.useSystemMenuBarProperty().set(true);
    }
    menuBar.getMenus().addAll(mainMenu, modelsMenu, helpMenu, settingsMenu);

    content.getTabs().add(terminalTab);

    BorderPane rootNode = new BorderPane();
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
    content
        .getTabs()
        .forEach(
            t -> {
              EventHandler closeEvent = t.getOnClosed();
              if (closeEvent != null) {
                closeEvent.handle(null);
              }
            });
    content.getTabs().clear();
  }
}
