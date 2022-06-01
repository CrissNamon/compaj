package com.hiddenproject.compaj.gui.util;

import java.io.*;
import com.hiddenproject.compaj.gui.*;
import javafx.stage.*;

public class FileUtils {

  public static final String[] COMPAJ_SCRIPT_EXT = new String[]{
      "*.cjn"
  };
  private static FileUtils INSTANCE;

  private FileUtils() {

  }

  public static FileUtils getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new FileUtils();
    }
    return INSTANCE;
  }

  public static File saveFileWindow() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(AppSettings.getInstance().getAppDirectory());
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("CompaJ notes (*.cjn)", COMPAJ_SCRIPT_EXT);
    fileChooser.getExtensionFilters().add(extFilter);
    return fileChooser.showSaveDialog(Compaj.getMainStage());
  }

  public static File openFileWindow() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(AppSettings.getInstance().getAppDirectory());
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("CompaJ notes (*.cjn)", COMPAJ_SCRIPT_EXT);
    fileChooser.getExtensionFilters().add(extFilter);
    return fileChooser.showOpenDialog(Compaj.getMainStage());
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
      try (BufferedReader br
               = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
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
