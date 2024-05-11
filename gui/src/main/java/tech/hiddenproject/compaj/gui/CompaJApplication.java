package tech.hiddenproject.compaj.gui;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.compaj.gui.app.AppSettings;
import tech.hiddenproject.compaj.gui.event.UiChildPayload;
import tech.hiddenproject.compaj.gui.event.UiMenuEvent;
import tech.hiddenproject.compaj.gui.menu.MenuHolder;
import tech.hiddenproject.compaj.gui.tab.EditorTab;
import tech.hiddenproject.compaj.gui.tab.TabHolder;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.view.StageHolder;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

public class CompaJApplication extends Application {

  public static void launch(String... args) {
    Application.launch(args);
  }

  private static final String TAB_EDITOR_TITLE = "tab.editor.title";
  private static final String TAB_TERMINAL_TITLE = "tab.terminal.title";
  private static final String TAB_WORKSPACE_TITLE = "tab.workspace.title";
  private static final String MENU_VIEW = "menu.view";
  private static final String MENU_HELP = "menu.help";
  private static final String MENU_SETTINGS = "menu.settings";

  @Override
  public void start(Stage stage) {
    StageHolder stageHolder = StageHolder.createInstance(stage);
    MenuHolder menuHolder = MenuHolder.getInstance();
    stage.setTitle(AppSettings.APP_TITLE);

    MenuItem editorItem = new MenuItem(I18nUtils.get(TAB_EDITOR_TITLE));
    editorItem.setOnAction(actionEvent -> stageHolder.getContent().getTabs().add(new EditorTab()));
    Menu mainMenu = new Menu(I18nUtils.get(MENU_VIEW));
    mainMenu.getItems().addAll(editorItem);

    Menu helpMenu = new Menu(I18nUtils.get(MENU_HELP));
    Menu settingsMenu = new Menu(I18nUtils.get(MENU_SETTINGS));

    Menu terminalSettingsMenu = new Menu(I18nUtils.get(TAB_TERMINAL_TITLE));
    Menu editorSettingsMenu = new Menu(I18nUtils.get(TAB_EDITOR_TITLE));
    Menu workSpaceSettingsMenu = new Menu(I18nUtils.get(TAB_WORKSPACE_TITLE));
    settingsMenu.getItems().addAll(terminalSettingsMenu, editorSettingsMenu, workSpaceSettingsMenu);

    menuHolder.put("menu.help", helpMenu);
    menuHolder.put("menu.settings", settingsMenu);
    menuHolder.put("menu.settings.terminal", terminalSettingsMenu);
    menuHolder.put("menu.settings.editor", editorSettingsMenu);
    menuHolder.put("menu.settings.workspace", workSpaceSettingsMenu);

    menuHolder.addRootMenus(mainMenu, helpMenu, settingsMenu);

    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.ADD_ROOT_NAME,
                                        menu -> menuHolder.addRootMenu(menu.getPayload()));
    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.ADD_CHILD_NAME, this::addChildMenu);
    EventPublisher.INSTANCE.sendTo(UiMenuEvent.STARTUP);

    stageHolder.getContent().getTabs().addAll(TabHolder.INSTANCE.getTerminalTab(),
                                              TabHolder.INSTANCE.getWorkSpaceTab());

    BorderPane rootNode = new BorderPane();
    rootNode.setTop(menuHolder.getMenuBar());
    rootNode.setCenter(stageHolder.getContent());

    Scene scene = new Scene(rootNode, 1280, 720);
    scene.getStylesheets().add(AppSettings.getInstance().getStyleSheet());
    new JMetro(Style.LIGHT).setScene(scene);
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
    StageHolder.getInstance().getContent().getTabs().forEach(this::closeTab);
    StageHolder.getInstance().getContent().getTabs().clear();
  }

  private void addChildMenu(CompaJEvent event) {
    MenuHolder menuHolder = MenuHolder.getInstance();
    UiChildPayload uiChildPayload = event.getPayload();
    BooleanOptional.of(menuHolder.contains(uiChildPayload.getRootId()))
        .ifTrueThen(() -> menuHolder.get(uiChildPayload.getRootId())
            .getItems().add(uiChildPayload.getNode()));
  }

  private void closeTab(Tab tab) {
    EventHandler<Event> closeEvent = tab.getOnClosed();
    if (closeEvent != null) {
      closeEvent.handle(null);
    }
  }

}
