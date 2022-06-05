package tech.hiddenproject.compaj.gui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javafx.stage.FileChooser;
import tech.hiddenproject.compaj.gui.AppSettings;
import tech.hiddenproject.compaj.gui.Compaj;

public class FileUtils {

  public static final String[] COMPAJ_SCRIPT_EXT = new String[] {"*.cjn"};
  private static FileUtils INSTANCE;

  private FileUtils() {}

  public static FileUtils getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new FileUtils();
    }
    return INSTANCE;
  }

  public static File saveFileWindow(String fileType, String fileExt) {
    return saveFileWindow(fileType, new String[] {fileExt});
  }

  public static File saveFileWindow(String fileType, String[] fileExt) {
    return createChooser(AppSettings.getInstance().getAppDirectory(), fileType, fileExt)
        .showSaveDialog(Compaj.getMainStage());
  }

  private static FileChooser createChooser(File init, String fileType, String[] fileExt) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(init);
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(fileType, fileExt);
    fileChooser.getExtensionFilters().add(extFilter);
    return fileChooser;
  }

  public static File openFileWindow(String fileType, String fileExt) {
    return openFileWindow(fileType, new String[] {fileExt});
  }

  public static File openFileWindow(String fileType, String[] fileExt) {
    return createChooser(AppSettings.getInstance().getAppDirectory(), fileType, fileExt)
        .showOpenDialog(Compaj.getMainStage());
  }

  public static File openNoteWindow() {
    return openFileWindow("CompaJ Note", COMPAJ_SCRIPT_EXT);
  }

  public static File saveNoteWindow() {
    return saveFileWindow("CompaJ Note", COMPAJ_SCRIPT_EXT);
  }

  public static void saveFile(File file, String data) {
    try {
      PrintWriter writer;
      writer = new PrintWriter(file);
      writer.println(data);
      writer.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public static String readFile(File file) {
    try {
      StringBuilder resultStringBuilder = new StringBuilder();
      try (BufferedReader br =
          new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
        String line;
        while ((line = br.readLine()) != null) {
          resultStringBuilder.append(line).append("\n");
        }
      }
      return resultStringBuilder.toString();
    } catch (IOException e) {
      throw new RuntimeException("Exception with file reader!");
    }
  }
}
