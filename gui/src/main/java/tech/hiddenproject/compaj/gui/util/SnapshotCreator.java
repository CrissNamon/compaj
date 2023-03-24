package tech.hiddenproject.compaj.gui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class SnapshotCreator {

  public static void exportPngSnapshot(Node node, Path path, Paint backgroundFill)
      throws IOException {
    SnapshotParameters params = new SnapshotParameters();
    params.setFill(backgroundFill);
    Image chartSnapshot = node.snapshot(params, null);
    PngEncoderFX encoder = new PngEncoderFX(chartSnapshot, true);
    byte[] bytes = encoder.pngEncode();
    Files.write(path, bytes);
  }
}
