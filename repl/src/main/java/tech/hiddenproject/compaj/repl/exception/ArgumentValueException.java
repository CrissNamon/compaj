package tech.hiddenproject.compaj.repl.exception;

/**
 * Thrown on CLI arguments parsing error.
 */
public class ArgumentValueException extends RuntimeException {

  public ArgumentValueException(String message) {
    super(message);
  }
}
