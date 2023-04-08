package tech.hiddenproject.compaj.gui.component;

import java.util.Objects;

import com.sun.javafx.scene.control.ContextMenuContent;
import com.sun.javafx.scene.control.ContextMenuContent.MenuItemContainer;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.WhenConditional;
import tech.hiddenproject.compaj.gui.suggestion.Suggestion;

/**
 * {@link ContextMenu} for {@link tech.hiddenproject.compaj.gui.suggestion.SuggestCore}.
 */
public class SuggestContextMenu extends ContextMenu {

  private final Popup javadocPopup = new Popup();
  private EventHandler<? super KeyEvent> oldHandler;

  public SuggestContextMenu() {
    addEventHandler(Menu.ON_SHOWING, this::onShowingHandler);
    addEventHandler(Menu.ON_SHOWN, this::onShowHandler);
    setOnCloseRequest(windowEvent -> javadocPopup.hide());
  }

  /**
   * Creates new {@link ContextMenuBuilder}.
   *
   * @return {@link ContextMenuBuilder} for {@link SuggestContextMenu}.
   */
  public static ContextMenuBuilder create() {
    return new ContextMenuBuilder(new SuggestContextMenu());
  }

  private void onShowingHandler(Event event) {
    Node content = getSkin().getNode();
    WhenConditional.create()
        .when(content instanceof Region).then(() -> ((Region) content).setMaxHeight(200))
        .orDoNothing();
  }

  private void onShowHandler(Event event) {
    ContextMenuContent content = (ContextMenuContent) getSkin().getNode();
    oldHandler = IfTrueConditional.create()
        .ifTrue(Objects.isNull(oldHandler)).then(content::getOnKeyPressed)
        .orElse(oldHandler);
    content.requestFocusOnIndex(0);
    content.setOnKeyPressed(keyEvent -> WhenConditional.create()
        .when(isShowJavadocKey(keyEvent)).then(() -> showJavadoc(keyEvent))
        .when(isHideJavadocKey(keyEvent)).then(this::hideAll)
        .when(isShouldBeHandled(keyEvent.getCode())).then(() -> handleKeyEvent(keyEvent))
        .orElseDo(this::hideAll));
  }

  private boolean isShowJavadocKey(KeyEvent keyEvent) {
    return keyEvent.getCode().equals(KeyCode.RIGHT);
  }

  private boolean isHideJavadocKey(KeyEvent keyEvent) {
    return keyEvent.getCode().equals(KeyCode.LEFT);
  }

  private void showJavadoc(KeyEvent keyEvent) {
    MenuItemContainer menuItemContainer = (MenuItemContainer) keyEvent.getTarget();
    Suggestion suggestion = (Suggestion) menuItemContainer.getItem().getUserData();
    WhenConditional.create()
        .when(Objects.nonNull(suggestion) && !suggestion.getJavadoc().isEmpty())
        .then(() -> showJavadocPopup(suggestion.getJavadoc()))
        .orElseDo(javadocPopup::hide);
  }

  private void showJavadocPopup(String javadoc) {
    javadocPopup.getContent().clear();
    Label label = new Label(javadoc);
    javadocPopup.getContent().add(label);
    javadocPopup.show(this, getX() + getWidth(), getY());
  }

  private void hideAll() {
    hide();
    javadocPopup.hide();
  }

  private boolean isShouldBeHandled(KeyCode keyCode) {
    return keyCode.equals(KeyCode.ENTER) || keyCode.equals(KeyCode.DOWN)
        || keyCode.equals(KeyCode.UP);
  }

  private void handleKeyEvent(KeyEvent keyEvent) {
    if (Objects.nonNull(oldHandler)) {
      javadocPopup.hide();
      oldHandler.handle(keyEvent);
    } else {
      hideAll();
    }
  }
}
