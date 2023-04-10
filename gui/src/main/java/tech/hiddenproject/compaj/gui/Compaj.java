package tech.hiddenproject.compaj.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.compaj.extension.AgentExtension;
import tech.hiddenproject.compaj.extension.ArrayRealVectorExtension;
import tech.hiddenproject.compaj.extension.CompaJComplex;
import tech.hiddenproject.compaj.extension.MathExtension;
import tech.hiddenproject.compaj.extension.ModelExtension;
import tech.hiddenproject.compaj.extension.NamedFunctionExtension;
import tech.hiddenproject.compaj.extension.StarterExtension;
import tech.hiddenproject.compaj.gui.app.AppSettings;
import tech.hiddenproject.compaj.gui.tab.TabHolder;
import tech.hiddenproject.compaj.gui.tab.WorkSpaceTab;
import tech.hiddenproject.compaj.gui.util.FileViewUtils;
import tech.hiddenproject.compaj.gui.view.StageHolder;
import tech.hiddenproject.compaj.gui.widget.BaseWidget;
import tech.hiddenproject.compaj.gui.widget.WorkSpaceWidget;
import tech.hiddenproject.compaj.lang.FileUtils;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.CompaJScriptBase;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslator;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;

public class Compaj {

  public static final Logger LOGGER = LoggerFactory.getLogger(Compaj.class);
  private static final AppSettings APP_SETTINGS = AppSettings.getInstance();

  private static Compaj INSTANCE;

  private final GroovyTranslator translator;

  private Compaj(GroovyTranslator translator) {
    this.translator = translator;
  }

  static void init() {
    if (Objects.isNull(INSTANCE)) {
      FileFilter libFilter = APP_SETTINGS.pluginsFileFilter();
      File[] librariesFiles = Optional.ofNullable(
              APP_SETTINGS.getLibrariesDirectory().listFiles(libFilter))
          .orElse(new File[]{});
      List<String> librariesPaths = Arrays.stream(librariesFiles)
          .map(File::getAbsolutePath)
          .collect(Collectors.toList());
      initTranslator();
      LOGGER.info("Found libs: {}", librariesPaths);
      TranslatorUtils translatorUtils = new GroovyTranslatorUtils();
      GroovyTranslator groovyTranslator = new GroovyTranslator(translatorUtils, librariesPaths,
                                                               APP_SETTINGS.getPluginsDirectory()
                                                                   .getAbsolutePath(),
                                                               TranslatorProperties.DEFAULT_TMP_FILE);
      INSTANCE = new Compaj(groovyTranslator);
    }
  }

  public static Compaj getInstance() {
    if (Objects.isNull(INSTANCE)) {
      init();
    }
    return INSTANCE;
  }

  /**
   * Creates widget from {@link Node} and adds it to WorkSpace.
   *
   * @param name Widget name
   * @param node {@link Node}
   */
  public static void addWorkSpaceWidget(String name, Node node) {
    addWorkSpaceWidget(new BaseWidget(node, name));
  }

  /**
   * Adds widget to WorkSpace.
   *
   * @param workSpaceWidget {@link WorkSpaceWidget}
   */
  public static void addWorkSpaceWidget(WorkSpaceWidget workSpaceWidget) {
    WorkSpaceTab workSpaceTab = TabHolder.INSTANCE.getWorkSpaceTab();
    workSpaceTab.addItem(workSpaceWidget);
    TabPane content = StageHolder.getInstance().getContent();
    if (!content.getTabs().contains(workSpaceTab)) {
      content.getTabs().add(workSpaceTab);
    }
  }

  /**
   * Creates widget from {@link Node} and adds it to WorkSpace.
   *
   * @param node {@link Node}
   */
  public static void addWorkSpaceWidget(Node node) {
    addWorkSpaceWidget(new BaseWidget(node));
  }

  /**
   * Evaluates script from file.
   *
   * @param path Script path
   * @return Evaluation result
   */
  public static Object exec(String path) {
    File f = new File(path);
    return INSTANCE.getTranslator().evaluate(FileUtils.readFromFile(f));
  }

  /**
   * Evaluates script from file using file picker.
   *
   * @return Evaluation result
   */
  public static Object exec() {
    File f = FileViewUtils.openNoteWindow();
    return INSTANCE.getTranslator().evaluate(FileUtils.readFromFile(f));
  }

  private static void initTranslator() {
    Imports.normalImports.addAll(
        Set.of(
            CompaJComplex.class.getCanonicalName(),
            Compaj.class.getCanonicalName()
        )
    );
    Imports.starImports.addAll(Set.of(
        "tech.hiddenproject.compaj.gui",
        "tech.hiddenproject.compaj.gui.widget",
        "tech.hiddenproject.compaj.gui.component"
    ));
    CompaJScriptBase.addExtension(new StarterExtension());
    CompaJScriptBase.addExtension(new MathExtension());
    CompaJScriptBase.addExtension(new ArrayRealVectorExtension());
    CompaJScriptBase.addExtension(new ModelExtension());
    CompaJScriptBase.addExtension(new NamedFunctionExtension());
    CompaJScriptBase.addExtension(new AgentExtension());
  }

  /**
   * @return {@link GroovyTranslator}
   */
  public GroovyTranslator getTranslator() {
    return translator;
  }

}
