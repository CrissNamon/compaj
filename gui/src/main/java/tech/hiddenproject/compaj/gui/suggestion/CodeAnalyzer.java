package tech.hiddenproject.compaj.gui.suggestion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.WhenConditional;
import tech.hiddenproject.compaj.gui.Compaj;
import tech.hiddenproject.compaj.gui.component.LineChartBuilder;
import tech.hiddenproject.compaj.gui.component.TableBuilder;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;

/**
 * Analyzes incomplete source code to extract variables, classes, functions and
 * define their types for {@link SuggestCore}.
 */
public class CodeAnalyzer {

  private static final Logger LOGGER = LoggerFactory.getLogger(CodeAnalyzer.class);
  private static final String VAR_GROUP = "VAR";
  private static final String STRING_GROUP = "STRING";
  private static final String DOUBLE_GROUP = "DOUBLE";
  private static final String INTEGER_GROUP = "INTEGER";
  private static final String ASSIGNMENT_GROUP = "VARIABLE";
  private static final String CONSTRUCTOR_GROUP = "CONSTRUCTOR";
  private static final String FUNCTION_TYPE_GROUP = "FUNCTIONTYPE";
  private static final String FUNCTION_NAME_GROUP = "FUNCTIONNAME";
  private static final String UNFORMATTED_PATTERN = "((?<%s>\\w+)[ ]*=[ ]*("
      + "(?<%s>(?>[ \\n\\t\\d\\w]*\\++[ ]*)*\"[\\w\\d!?\\s;,.\\/]++\")"
      + "|(?<%s>[\\d]++\\.[\\d]++)"
      + "|(?<%s>[\\d]++(?![^;\\s]))"
      + "|(?<%s>(?!new)[a-zA-Z][\\w\\d]*)"
      + "|(?>(new[ ]|:)(?<%s>\\w[\\w\\d]++))))"
      + "|(?<FUNCTION>(?<%s>\\w\\w*)[ ]+(?<%s>\\w+)[\\s]*\\()";
  private static final Pattern PATTERN =
      Pattern.compile(String.format(UNFORMATTED_PATTERN, VAR_GROUP, STRING_GROUP, DOUBLE_GROUP,
                                    INTEGER_GROUP, ASSIGNMENT_GROUP, CONSTRUCTOR_GROUP,
                                    FUNCTION_TYPE_GROUP, FUNCTION_NAME_GROUP),
                      Pattern.MULTILINE | Pattern.UNICODE_CASE);

  private static final PatriciaTrie<Class<?>> EXISTED_CLASSES = new PatriciaTrie<>();

  static {
    EXISTED_CLASSES.putAll(
        Map.of("Double", Double.class, "Integer", Integer.class,
               "Boolean", Boolean.class, "String", String.class)
    );
    EXISTED_CLASSES.putAll(
        Map.of("Stream", Stream.class, "Arrays", Arrays.class)
    );
    EXISTED_CLASSES.putAll(
        Map.of("Supplier", Supplier.class, "Function", Function.class,
               "Consumer", Consumer.class, "Predicate", Predicate.class)
    );
    EXISTED_CLASSES.putAll(
        Map.of("Compaj", Compaj.class, "LineChartBuilder", LineChartBuilder.class,
               "TableBuilder", TableBuilder.class)
    );
    Imports.normalImports.stream()
        .map(CodeAnalyzer::getClassByName)
        .filter(Objects::nonNull)
        .forEach(c -> EXISTED_CLASSES.put(c.getSimpleName(), c));
  }

  private final PatriciaTrie<Class<?>> classes = new PatriciaTrie<>();
  private final PatriciaTrie<Class<?>> methods = new PatriciaTrie<>();
  private final PatriciaTrie<Class<?>> variables = new PatriciaTrie<>();
  private final PatriciaTrie<Class<?>> fields = new PatriciaTrie<>();

  /**
   * Creates full name of {@link Method}.
   *
   * @param method {@link Method}
   * @return Method name
   */
  public static String createMethodName(Method method) {
    return method.getName()
        + Arrays.toString(method.getParameterTypes())
        .replace("[", "(").replace("]", ")");
  }

  /**
   * Creates new analyzer and analyzes input text.
   *
   * @param input Source code
   * @return {@link CodeAnalyzer}
   */
  public static CodeAnalyzer create(String input) {
    CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
    codeAnalyzer.analyze(input);
    return codeAnalyzer;
  }

  /**
   * Gets found classes.
   *
   * @return {@link PatriciaTrie}
   */
  public PatriciaTrie<Class<?>> getClasses() {
    return classes;
  }

  /**
   * Adds new classes.
   *
   * @param classes {@link Class}
   */
  public void addClasses(Set<Class<?>> classes) {
    this.classes.putAll(
        classes.stream()
            .collect(Collectors.toMap(Class::getSimpleName, c -> c))
    );
  }

  /**
   * Gets found method types.
   *
   * @return {@link PatriciaTrie}
   */
  public PatriciaTrie<Class<?>> getMethods() {
    return methods;
  }

  /**
   * Adds new method types.
   *
   * @param methods {@link Method}
   */
  public void addMethods(Set<Method> methods) {
    this.methods.putAll(
        methods.stream()
            .collect(Collectors.toMap(CodeAnalyzer::createMethodName, Method::getReturnType, (v1, v2) -> v2))
    );
  }

  /**
   * Gets found field types.
   *
   * @return {@link PatriciaTrie}
   */
  public PatriciaTrie<Class<?>> getFields() {
    return fields;
  }

  /**
   * Adds new field types.
   *
   * @param fields {@link Field}
   */
  public void addFields(Set<Field> fields) {
    this.fields.putAll(
        fields.stream()
            .collect(Collectors.toMap(Field::getName, Field::getType))
    );
  }

  /**
   * Gets found variable types.
   *
   * @return {@link PatriciaTrie}
   */
  public PatriciaTrie<Class<?>> getVariables() {
    return variables;
  }

  private void analyze(String input) {
    classes.clear();
    methods.clear();
    variables.clear();
    Matcher matcher = PATTERN.matcher(input);
    while (matcher.find()) {
      if (Objects.nonNull(matcher.group(VAR_GROUP))) {
        Class<?> c = IfTrueConditional.create()
            .ifTrue(Objects.nonNull(matcher.group(STRING_GROUP))).then(() -> String.class)
            .ifTrue(Objects.nonNull(matcher.group(DOUBLE_GROUP))).then(() -> Double.class)
            .ifTrue(Objects.nonNull(matcher.group(INTEGER_GROUP))).then(() -> Integer.class)
            .ifTrue(Objects.nonNull(matcher.group(ASSIGNMENT_GROUP))
                        && variables.containsKey(matcher.group(ASSIGNMENT_GROUP).trim()))
            .then(() -> variables.get(matcher.group(ASSIGNMENT_GROUP).trim()))
            .ifTrue(Objects.nonNull(matcher.group(CONSTRUCTOR_GROUP))
                        && isClassExists(matcher.group(CONSTRUCTOR_GROUP)))
            .then(() -> getExistedClass(matcher.group(CONSTRUCTOR_GROUP)))
            .orElse(null);
        WhenConditional.create()
            .when(Objects.nonNull(c)).then(() -> variables.put(matcher.group(VAR_GROUP), c))
            .orDoNothing();
      }
      if (Objects.nonNull(matcher.group(FUNCTION_NAME_GROUP))) {
        Class<?> c = EXISTED_CLASSES.get(matcher.group(FUNCTION_TYPE_GROUP).trim());
        BooleanOptional.of(Objects.nonNull(c))
            .ifTrueThen(() -> methods.put(matcher.group(FUNCTION_NAME_GROUP).trim(), c));
      }
    }
    classes.putAll(EXISTED_CLASSES);
  }

  private static Class<?> getClassByName(String name) {
    try {
      return Class.forName(name);
    } catch (Throwable t) {
      return null;
    }
  }

  private boolean isClassExists(String name) {
    return EXISTED_CLASSES.containsKey(name);
  }

  private Class<?> getExistedClass(String name) {
    return EXISTED_CLASSES.get(name);
  }

}
