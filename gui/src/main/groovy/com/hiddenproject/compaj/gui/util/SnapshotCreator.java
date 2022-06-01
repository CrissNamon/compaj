package com.hiddenproject.compaj.gui.util;

import java.io.*;
import java.nio.file.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.paint.*;

public class SnapshotCreator {
  public static void exportPngSnapshot(
      Node node,
      Path path,
      Paint backgroundFill
  ) throws IOException {
    SnapshotParameters params = new SnapshotParameters();
    params.setFill(backgroundFill);
    Image chartSnapshot = node.snapshot(params, null);
    PngEncoderFX encoder = new PngEncoderFX(chartSnapshot, true);
    byte[] bytes = encoder.pngEncode();
    Files.write(path, bytes);
  }
}
