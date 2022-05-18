package com.hiddenproject.compaj.repl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import com.hiddenproject.compaj.translator.Translator;
import com.hiddenproject.compaj.translator.TranslatorUtils;

public class CompaJ {

  private static CompaJ INSTANCE;
  private static ExitManager exitManager;

  private Translator translator;
  private TranslatorUtils translatorUtils;

  private CompaJ() {
    exitManager = new ExitManager();
    System.out.println("Welcome to CompaJ REPL!");
    System.out.println("Version 0.0.1");
  }

  public static CompaJ getInstance() {
    if(INSTANCE == null) {
      INSTANCE = new CompaJ();
    }
    return INSTANCE;
  }

  public void setTranslator(Translator translator) {
    this.translator = translator;
  }

  public void useCompaJSyntax(boolean f) {
    translatorUtils.useRawGroovy(!f);
  }

  public static void readFile(String url) {
    try {
      Path path = Paths.get(url);
      String script = new String(Files.readAllBytes(path));
      getInstance().getTranslator().evaluate(script);
    }catch (IOException e) {
      System.out.println("Error: " + e.getLocalizedMessage());
    }
    //System.out.println("READING: " + getInstance().getTranslator().evaluate(script));
  }

  public Translator getTranslator() {
    return translator;
  }

  public static void exit() {
    exitManager.reset();
    System.exit(0);
  }

  private static class ExitManager extends SecurityManager {

    private final SecurityManager DEFAULT;

    public ExitManager() {
      DEFAULT = System.getSecurityManager();
      System.setSecurityManager(this);
    }

    public void reset() {
      System.setSecurityManager(DEFAULT);
    }

    /** Deny permission to exit the VM. */
    public void checkExit(int status) {
      throw( new SecurityException() );
    }

    /** Allow this security manager to be replaced,
     if fact, allow pretty much everything. */
    public void checkPermission(Permission perm) {
    }
  }
}
