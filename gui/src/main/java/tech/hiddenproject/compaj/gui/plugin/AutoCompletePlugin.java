package tech.hiddenproject.compaj.gui.plugin;

import javafx.scene.control.MenuItem;
import tech.hiddenproject.compaj.gui.AppPreference;
import tech.hiddenproject.compaj.gui.event.UiChildPayload;
import tech.hiddenproject.compaj.gui.event.UiMenuEvent;
import tech.hiddenproject.compaj.gui.util.I18nUtils;
import tech.hiddenproject.compaj.plugin.api.CompaJPlugin;
import tech.hiddenproject.compaj.plugin.api.event.CompaJEvent;
import tech.hiddenproject.compaj.plugin.api.event.EventPublisher;

/**
 * Plugin for code completion settings.
 */
public class AutoCompletePlugin implements CompaJPlugin {

  public AutoCompletePlugin() {
    EventPublisher.INSTANCE.subscribeOn(UiMenuEvent.STARTUP_NAME, this::addSettingsOnStartup);
  }

  private void addSettingsOnStartup(CompaJEvent compaJEvent) {
    MenuItem codeCompletion = new MenuItem();
    updateCodeCompletionStateText(AppPreference.CODE_AUTOCOMPLETE.getOrDefault(true),
                                  codeCompletion);
    codeCompletion.setOnAction(actionEvent -> {
      Boolean isEnabled = AppPreference.CODE_AUTOCOMPLETE.getOrDefault(true);
      updateCodeCompletionStateText(!isEnabled, codeCompletion);
      AppPreference.CODE_AUTOCOMPLETE.update(!isEnabled);
    });
    UiChildPayload autoCompleteSettings = new UiChildPayload("menu.settings.editor",
                                                             codeCompletion);
    EventPublisher.INSTANCE.sendTo(UiMenuEvent.ADD_CHILD(autoCompleteSettings));
  }

  private void updateCodeCompletionStateText(Boolean isEnabled, MenuItem menuItem) {
    if (isEnabled) {
      menuItem.setText(I18nUtils.get("menu.settings.editor.autocomplete.disable"));
    } else {
      menuItem.setText(I18nUtils.get("menu.settings.editor.autocomplete.enable"));
    }
  }

}
