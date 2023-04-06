package tech.hiddenproject.compaj.gui.view;

import java.util.Objects;

import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import tech.hiddenproject.aide.optional.WhenConditional;
import tech.hiddenproject.compaj.gui.component.ContextMenuBuilder;

/**
 * {@link ContextMenu} for {@link tech.hiddenproject.compaj.gui.suggestion.SuggestCore}.
 */
public class SuggestContextMenu extends ContextMenu {

  private EventHandler<? super KeyEvent> oldHandler;

  /**
   * @return {@link ContextMenuBuilder} for {@link SuggestContextMenu}.
   */
  public static ContextMenuBuilder create() {
    return new ContextMenuBuilder(new SuggestContextMenu());
  }

  public SuggestContextMenu() {
    addEventHandler(Menu.ON_SHOWING, this::onShowingHandler);
    addEventHandler(Menu.ON_SHOWN, this::onShowHandler);
  }

  private void onShowingHandler(Event event) {
    Node content = getSkin().getNode();
    WhenConditional.create()
        .when(content instanceof Region).then(() -> ((Region) content).setMaxHeight(200))
        .orDoNothing();
  }

  private void onShowHandler(Event event) {
    ContextMenuContent content = (ContextMenuContent) getSkin().getNode();
    if (Objects.isNull(oldHandler)) {
      oldHandler = content.getOnKeyPressed();
    }
    content.setOnKeyPressed(keyEvent -> WhenConditional.create()
        .when(isInsertSuggestionKeyCode(keyEvent.getCode())).then(() -> oldHandler.handle(keyEvent))
        .orElseDo(this::hide));
  }

  private boolean isInsertSuggestionKeyCode(KeyCode keyCode) {
    return keyCode.equals(KeyCode.ENTER) || keyCode.equals(KeyCode.DOWN)
        || keyCode.equals(KeyCode.UP);
  }
}
