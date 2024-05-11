package tech.hiddenproject.compaj.gui.plugin;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.compaj.gui.Compaj;
import tech.hiddenproject.compaj.gui.event.UiMenuEvent;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.gui.widget.SIRModelWidget;
import tech.hiddenproject.compaj.gui.widget.SISModelWidget;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

/**
 * Create menus for epidemic modules.
 */
public class EpidemicModelPlugin implements CompaJPlugin {

  private static final Logger LOGGER = LoggerFactory.getLogger(EpidemicModelPlugin.class);
  private static final String MENU_SIMPLE_MODELS = "menu.simple_models";
  private static final String MENU_SIMPLE_MODELS_EPIDEMIC = "menu.simple_models.epidemic";

  public EpidemicModelPlugin() {
    LOGGER.info("Loading: {}", getClass().getName());
    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.STARTUP_NAME, this::createMenu);
  }

  private void createMenu(CompaJEvent event) {
    MenuItem sirModel = new MenuItem("SIR");
    sirModel.setOnAction(actionEvent -> Compaj.addWorkSpaceWidget(new SIRModelWidget()));

    MenuItem sisModel = new MenuItem("SIS");
    sisModel.setOnAction(actionEvent -> Compaj.addWorkSpaceWidget(new SISModelWidget()));

    MenuItem seirModel = new MenuItem("SEIR");
    Menu infectModels = new Menu(I18nUtils.get(MENU_SIMPLE_MODELS_EPIDEMIC));

    infectModels.getItems().addAll(sirModel, sisModel, seirModel);
    Menu modelsMenu = new Menu(I18nUtils.get(MENU_SIMPLE_MODELS));
    modelsMenu.getItems().add(infectModels);

    EventPublisher.INSTANCE.sendTo(UiMenuEvent.ADD_ROOT(modelsMenu));
  }
}
