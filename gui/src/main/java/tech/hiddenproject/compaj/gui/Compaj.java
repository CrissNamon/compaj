package tech.hiddenproject.compaj.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.compaj.extension.AgentExtension;
import tech.hiddenproject.compaj.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.extension.CompaJComplex;
import tech.hiddenproject.compaj.extension.ComplexExtension;
import tech.hiddenproject.compaj.extension.MathExtension;
import tech.hiddenproject.compaj.extension.ModelExtension;
import tech.hiddenproject.compaj.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.extension.StarterExtension;
import tech.hiddenproject.compaj.gui.event.UiChildPayload;
import tech.hiddenproject.compaj.gui.event.UiMenuEvent;
import tech.hiddenproject.compaj.gui.tab.EditorTab;
import tech.hiddenproject.compaj.gui.tab.TerminalTab;
import tech.hiddenproject.compaj.gui.tab.WorkSpaceTab;
import tech.hiddenproject.compaj.gui.util.FileUtils;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.widget.BaseWidget;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

public class Compaj extends Application {

  public static final Logger LOGGER = LoggerFactory.getLogger(Compaj.class);
  private static final AppSettings APP_SETTINGS = AppSettings.getInstance();
  private static final Map<String, Menu> ROOT_MENUS = new HashMap<>();
  private static GroovyTranslator translator;
  private static TabPane content;
  private static TerminalTab terminalTab;
  private static WorkSpaceTab workSpaceTab;
  private static Stage mainStage;
  private MenuBar menuBar;

  public static void main(String[] args) {
    FileFilter libFilter = APP_SETTINGS.pluginsFileFilter();
    File[] librariesFiles = Optional.ofNullable(
            APP_SETTINGS.getLibrariesDirectory().listFiles(libFilter))
        .orElse(new File[]{});
    List<String> librariesPaths = Arrays.stream(librariesFiles)
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
    initTranslator();
    LOGGER.info("Found libs: {}", librariesPaths);
    TranslatorUtils translatorUtils = new GroovyTranslatorUtils();
    translator = new GroovyTranslator(translatorUtils, librariesPaths,
                                      APP_SETTINGS.getPluginsDirectory().getAbsolutePath(),
                                      TranslatorProperties.DEFAULT_TMP_FILE);
    launch(args);
  }

  /**
   * Creates widget from {@link Node} and adds it to WorkSpace.
   *
   * @param name Widget name
   * @param node {@link Node}
   */
  public static void addWorkSpaceWidget(String name, Node node) {
    addWorkSpaceWidget(new BaseWidget(node, name));
  }

  /**
   * Adds widget to WorkSpace.
   *
   * @param workSpaceWidget {@link WorkSpaceWidget}
   */
  public static void addWorkSpaceWidget(WorkSpaceWidget workSpaceWidget) {
    workSpaceTab.addItem(workSpaceWidget);
    if (!content.getTabs().contains(workSpaceTab)) {
      content.getTabs().add(workSpaceTab);
    }
  }

  /**
   * Creates widget from {@link Node} and adds it to WorkSpace.
   *
   * @param node {@link Node}
   */
  public static void addWorkSpaceWidget(Node node) {
    addWorkSpaceWidget(new BaseWidget(node));
  }

  /**
   * Evaluates script from file.
   *
   * @param path Script path
   * @return Evaluation result
   */
  public static Object exec(String path) {
    File f = new File(path);
    return getTranslator().evaluate(FileUtils.readFile(f));
  }

  /**
   * @return {@link GroovyTranslator}
   */
  public static GroovyTranslator getTranslator() {
    return translator;
  }

  /**
   * Evaluates script from file using file picker.
   *
   * @return Evaluation result
   */
  public static Object exec() {
    File f = FileUtils.openNoteWindow();
    return getTranslator().evaluate(FileUtils.readFile(f));
  }

  /**
   * @return Main stage {@link Stage}
   */
  public static Stage getMainStage() {
    return mainStage;
  }

  private static void initTranslator() {
    Imports.normalImports.addAll(
        Set.of(
            CompaJComplex.class.getCanonicalName(),
            Compaj.class.getCanonicalName()
        )
    );
    Imports.starImports.addAll(Set.of(
        "tech.hiddenproject.compaj.gui",
        "tech.hiddenproject.compaj.gui.widget",
        "tech.hiddenproject.compaj.gui.component"
    ));
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  @Override
  public void start(Stage stage) {
    mainStage = stage;
    content = new TabPane();
    terminalTab = new TerminalTab();
    terminalTab.setClosable(false);
    workSpaceTab = new WorkSpaceTab();
    workSpaceTab.setClosable(false);

    stage.setTitle(AppSettings.APP_TITLE);

    MenuItem editorItem = new MenuItem(I18nUtils.get("tab.editor.title"));
    editorItem.setOnAction(actionEvent -> content.getTabs().add(new EditorTab()));
    Menu mainMenu = new Menu(I18nUtils.get("menu.view"));
    mainMenu.getItems().addAll(editorItem);

    Menu helpMenu = new Menu(I18nUtils.get("menu.help"));
    Menu settingsMenu = new Menu(I18nUtils.get("menu.settings"));

    ROOT_MENUS.put("menu.help", helpMenu);
    ROOT_MENUS.put("menu.settings", settingsMenu);

    menuBar = new MenuBar();
    final String os = System.getProperty("os.name");
    if (os != null && os.startsWith("Mac")) {
      menuBar.useSystemMenuBarProperty().set(true);
    }
    menuBar.getMenus().addAll(mainMenu, helpMenu, settingsMenu);

    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.ADD_ROOT_NAME, this::addRootMenu);
    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.ADD_CHILD_NAME, this::addChildMenu);
    EventPublisher.INSTANCE.sendTo(UiMenuEvent.STARTUP);

    content.getTabs().addAll(terminalTab, workSpaceTab);

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
    content.getTabs().forEach(this::closeTab);
    content.getTabs().clear();
  }

  private void addRootMenu(CompaJEvent event) {
    Menu menu = event.getPayload();
    menuBar.getMenus().add(menu);
  }

  private void addChildMenu(CompaJEvent event) {
    UiChildPayload uiChildPayload = event.getPayload();
    BooleanOptional.of(ROOT_MENUS.containsKey(uiChildPayload.getRootId()))
        .ifTrueThen(() -> ROOT_MENUS.get(uiChildPayload.getRootId())
            .getItems().add(uiChildPayload.getNode()));
  }

  private void closeTab(Tab tab) {
    EventHandler<Event> closeEvent = tab.getOnClosed();
    if (closeEvent != null) {
      closeEvent.handle(null);
    }
  }
}
