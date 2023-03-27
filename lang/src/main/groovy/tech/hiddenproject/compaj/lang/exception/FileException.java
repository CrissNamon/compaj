package tech.hiddenproject.compaj.lang.exception;

public class FileException extends RuntimeException {

  public FileException(String message) {
    super(message);
  }

  public FileException(Throwable cause) {
    super(cause);
  }
}
