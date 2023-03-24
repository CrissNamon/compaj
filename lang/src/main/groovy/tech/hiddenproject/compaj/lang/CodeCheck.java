package tech.hiddenproject.compaj.lang;

/**
 * Represents code check for some condition.
 */
public interface CodeCheck {

  /**
   * @param sourceCode Source code to check
   * @return true if given source code passed this check
   */
  boolean check(String sourceCode);
}
