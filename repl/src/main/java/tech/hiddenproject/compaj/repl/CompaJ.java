package tech.hiddenproject.compaj.repl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import tech.hiddenproject.compaj.extension.AgentExtension;
import tech.hiddenproject.compaj.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.extension.ComplexExtension;
import tech.hiddenproject.compaj.extension.MathExtension;
import tech.hiddenproject.compaj.extension.ModelExtension;
import tech.hiddenproject.compaj.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.extension.StarterExtension;
import tech.hiddenproject.compaj.lang.Translator;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.translation.MathTranslations;

public class CompaJ {

  private static final String[] normalImports =
      new String[] {
          "tech.hiddenproject.compaj.extension.MathExtension",
          "tech.hiddenproject.compaj.extension.CompaJComplex"
      };

  private static CompaJ INSTANCE;
  private static ExitManager exitManager;

  private TranslatorUtils translatorUtils;
  private Translator translator;

  {
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new ComplexExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  {
    GroovyTranslator.getImportCustomizer()
        .addImports(normalImports);
  }

  {
    translatorUtils.addCodeTranslation(MathTranslations::translateComplexNumbers);
    translatorUtils.addCodeTranslation(MathTranslations::translateMagicNumbers);
  }

  private CompaJ() {
    exitManager = new ExitManager();
    System.out.println("Welcome to CompaJ REPL!");
    System.out.println("Version 0.0.1");
  }

  public static void readFile(String url) {
    try {
      Path path = Paths.get(url);
      String script = new String(Files.readAllBytes(path));
      getInstance().getTranslator().evaluate(script);
    } catch (IOException e) {
      System.out.println("Error: " + e.getLocalizedMessage());
    }
  }

  public Translator getTranslator() {
    return translator;
  }

  public static CompaJ getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CompaJ();
    }
    return INSTANCE;
  }

  public void setTranslator(Translator translator) {
    this.translator = translator;
  }

  public void setTranslatorUtils(TranslatorUtils translatorUtils) {
    this.translatorUtils = translatorUtils;
  }

  public static void exit() {
    exitManager.reset();
    System.exit(0);
  }
}
