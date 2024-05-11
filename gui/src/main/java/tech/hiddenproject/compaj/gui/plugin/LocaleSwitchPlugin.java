package tech.hiddenproject.compaj.gui.plugin;

import java.util.Locale;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.compaj.gui.event.UiChildPayload;
import tech.hiddenproject.compaj.gui.event.UiMenuEvent;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

/**
 * Creates language menus.
 */
public class LocaleSwitchPlugin implements CompaJPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocaleSwitchPlugin.class);
  private static final String MENU_SETTINGS_LANG = "menu.settings.lang";

  public LocaleSwitchPlugin() {
    LOGGER.info("Loading: {}", getClass().getName());
    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.STARTUP_NAME, this::createMenu);
  }

  private void createMenu(CompaJEvent event) {
    MenuItem enLang = new MenuItem("English");
    enLang.setOnAction(actionEvent -> I18nUtils.changeLang(Locale.US));
    MenuItem ruLang = new MenuItem("Русский");
    ruLang.setOnAction(actionEvent -> I18nUtils.changeLang(Locale.forLanguageTag("ru-RU")));
    Menu languageMenu = new Menu(I18nUtils.get(MENU_SETTINGS_LANG));
    languageMenu.getItems().addAll(enLang, ruLang);

    UiChildPayload uiChildPayload = new UiChildPayload("menu.settings", languageMenu);
    EventPublisher.INSTANCE.sendTo(UiMenuEvent.ADD_CHILD(uiChildPayload));
  }
}
