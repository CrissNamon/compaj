package tech.hiddenproject.compaj.gui.util;

import java.io.File;

import javafx.stage.FileChooser;
import tech.hiddenproject.compaj.gui.app.AppSettings;
import tech.hiddenproject.compaj.gui.view.StageHolder;

public class FileViewUtils {

  protected static final String[] COMPAJ_SCRIPT_EXT = new String[]{"*.cjn"};

  private FileViewUtils() {
  }

  public static File saveFileWindow(String fileType, String fileExt) {
    return saveFileWindow(fileType, new String[]{fileExt});
  }

  public static File saveFileWindow(String fileType, String[] fileExt) {
    return createChooser(AppSettings.getInstance().getAppDirectory(), fileType, fileExt)
        .showSaveDialog(StageHolder.getInstance().getStage());
  }

  private static FileChooser createChooser(File init, String fileType, String[] fileExt) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(init);
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(fileType, fileExt);
    fileChooser.getExtensionFilters().add(extFilter);
    return fileChooser;
  }

  public static File openFileWindow(String fileType, String fileExt) {
    return openFileWindow(fileType, new String[]{fileExt});
  }

  public static File openFileWindow(String fileType, String[] fileExt) {
    return createChooser(AppSettings.getInstance().getAppDirectory(), fileType, fileExt)
        .showOpenDialog(StageHolder.getInstance().getStage());
  }

  public static File openNoteWindow() {
    return openFileWindow("CompaJ Note", COMPAJ_SCRIPT_EXT);
  }

  public static File saveNoteWindow() {
    return saveFileWindow("CompaJ Note", COMPAJ_SCRIPT_EXT);
  }

}
