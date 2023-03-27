package tech.hiddenproject.compaj.lang;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.compaj.lang.exception.FileException;

/**
 * Utils for files.
 */
public class FileUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

  /**
   * Converts string path to {@link URL}.
   *
   * @param path Path to file
   * @return {@link URL}
   */
  public static URL getFileUrl(String path) {
    try {
      File file = new File(path);
      BooleanOptional.of(file.exists())
          .ifFalseThrow(() -> new FileException("File not found: " + path));
      return file.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new FileException(e);
    }
  }

  /**
   * Writes to file with lock.
   *
   * @param file {@link File}
   * @param data Data to write
   */
  public static void writeToFile(File file, String data) {
    file.getParentFile().mkdirs();
    try (RandomAccessFile stream = new RandomAccessFile(file.getPath(), "rw");
         FileChannel channel = stream.getChannel();
         FileLock fileLock = channel.tryLock()) {
      file.createNewFile();
      stream.setLength(0);
      stream.write(data.getBytes());
    } catch (IOException e) {
      LOGGER.error("IO Error", e);
      throw new FileException(e);
    }
  }

  /**
   * Reads from file with lock.
   *
   * @param file {@link File}
   * @return File data
   */
  public static String readFromFile(File file) {
    try (RandomAccessFile stream = new RandomAccessFile(file.getPath(), "rw");
         FileChannel channel = stream.getChannel();
         FileLock fileLock = channel.tryLock()) {
      file.createNewFile();
      StringBuilder res = new StringBuilder();
      int b = stream.read();
      while (b != -1) {
        res.append((char) b);
        b = stream.read();
      }
      return res.toString();
    } catch (IOException e) {
      throw new FileException(e);
    }
  }
}
