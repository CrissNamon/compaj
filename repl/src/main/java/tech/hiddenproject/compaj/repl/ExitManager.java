package tech.hiddenproject.compaj.repl;

import java.security.Permission;

public class ExitManager extends SecurityManager {

  private final SecurityManager DEFAULT;

  public ExitManager() {
    DEFAULT = System.getSecurityManager();
    System.setSecurityManager(this);
  }

  public void reset() {
    System.setSecurityManager(DEFAULT);
  }

  /** Allow this security manager to be replaced, if fact, allow pretty much everything. */
  public void checkPermission(Permission perm) {}

  /** Deny permission to exit the VM. */
  public void checkExit(int status) {
    throw (new SecurityException());
  }
}
