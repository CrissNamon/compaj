package tech.hiddenproject.compaj.gui.suggestion;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.aide.optional.BooleanOptional;
import tech.hiddenproject.aide.optional.IfTrueConditional;
import tech.hiddenproject.aide.optional.WhenConditional;
import tech.hiddenproject.compaj.lang.groovy.TranslatorProperties.Imports;

/**
 * {@link Suggester} for classes, methods, fields names.
 */
public class VariableMethodsSuggester implements Suggester {

  private static final Logger LOGGER = LoggerFactory.getLogger(VariableMethodsSuggester.class);
  private static final String VAR_GROUP = "VAR";
  private static final String STRING_GROUP = "STRING";
  private static final String DOUBLE_GROUP = "DOUBLE";
  private static final String INTEGER_GROUP = "INTEGER";
  private static final String ASSIGNMENT_GROUP = "VARIABLE";
  private static final String CONSTRUCTOR_GROUP = "CONSTRUCTOR";
  private static final String FUNCTION_TYPE_GROUP = "FUNCTIONTYPE";
  private static final String FUNCTION_NAME_GROUP = "FUNCTIONNAME";
  private static final String UNFORMATTED_PATTERN = "((?<%s>\\w+)[ ]*=[ ]*("
      + "(?<%s>(?>[ \\n\\t\\d\\w]*\\++[ ]*)*\"[\\w\\d!?\\s]++\")"
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

  private static final Map<String, Class<?>> EXISTED_CLASSES = new HashMap<>();

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
    Imports.normalImports.stream()
        .map(VariableMethodsSuggester::getClassByName)
        .filter(Objects::nonNull)
        .forEach(c -> EXISTED_CLASSES.put(c.getSimpleName(), c));
  }

  private final Map<String, Class<?>> classMap = new HashMap<>();

  private static Class<?> getClassByName(String name) {
    try {
      return Class.forName(name);
    } catch (Throwable t) {
      return null;
    }
  }

  public VariableMethodsSuggester() {
    classMap.putAll(EXISTED_CLASSES);
  }

  @Override
  public Set<String> predict(String input, String prefix, int position) {
    analyze(input);
    if (input.contains(".")) {
      return suggest(prefix + " ");
    }
    return classMap.keySet().stream()
        .filter(aClass -> aClass.startsWith(prefix))
        .collect(Collectors.toSet());
  }

  private Set<String> suggest(String input) {
    Set<String> suggestions = new HashSet<>();
    String[] chain = (input + " ").split("\\.");
    for (int i = 0; i < chain.length - 1; i++) {
      String s1 = chain[i].trim();
      String s2 = chain[i + 1].trim();
      if (classMap.containsKey(s1)) {
        Class<?> c = classMap.get(s1);
        suggestions.addAll(getMethods(c, s2));
        suggestions.addAll(getFields(c, s2));
        suggestions.addAll(getInnerClasses(c, s2));
      }
    }
    classMap.clear();
    return suggestions;
  }

  private Set<String> getMethods(Class<?> parent, String prefix) {
    return Arrays.stream(parent.getDeclaredMethods())
        .filter(method -> isShouldBeCaptured(parent, method, prefix))
        .map(this::createMethodName)
        .collect(Collectors.toSet());
  }

  private Set<String> getFields(Class<?> parent, String prefix) {
    return Arrays.stream(parent.getDeclaredFields())
        .filter(field -> Modifier.isPublic(field.getModifiers())
            && field.getName().startsWith(prefix)
            && isStatic(parent, prefix, field))
        .map(Field::getName)
        .collect(Collectors.toSet());
  }

  private Set<String> getInnerClasses(Class<?> parent, String prefix) {
    return Arrays.stream(parent.getDeclaredClasses())
        .filter(inner -> Modifier.isPublic(inner.getModifiers())
            && inner.getSimpleName().startsWith(prefix))
        .map(Class::getSimpleName)
        .collect(Collectors.toSet());
  }

  private boolean isShouldBeCaptured(Class<?> parent, Member member, String prefix) {
    return Modifier.isPublic(member.getModifiers()) && member.getName().startsWith(prefix)
        && isStatic(parent, prefix, member);
  }

  private String createMethodName(Method method) {
    return method.getName()
        + Arrays.toString(method.getParameterTypes())
        .replace("[", "(").replace("]", ")");
  }

  private void analyze(String input) {
    Matcher matcher = PATTERN.matcher(input);
    while (matcher.find()) {
      if (Objects.nonNull(matcher.group(VAR_GROUP))) {
        Class<?> c = IfTrueConditional.create()
            .ifTrue(Objects.nonNull(matcher.group(STRING_GROUP))).then(() -> String.class)
            .ifTrue(Objects.nonNull(matcher.group(DOUBLE_GROUP))).then(() -> Double.class)
            .ifTrue(Objects.nonNull(matcher.group(INTEGER_GROUP))).then(() -> Integer.class)
            .ifTrue(Objects.nonNull(matcher.group(ASSIGNMENT_GROUP))
                        && classMap.containsKey(matcher.group(ASSIGNMENT_GROUP).trim()))
            .then(() -> classMap.get(matcher.group(ASSIGNMENT_GROUP).trim()))
            .ifTrue(Objects.nonNull(matcher.group(CONSTRUCTOR_GROUP))
                        && isClassExists(matcher.group(CONSTRUCTOR_GROUP)))
            .then(() -> getExistedClass(matcher.group(CONSTRUCTOR_GROUP)))
            .orElse(null);
        WhenConditional.create()
            .when(Objects.nonNull(c)).then(() -> classMap.put(matcher.group(VAR_GROUP), c))
            .orDoNothing();
      }
      if (Objects.nonNull(matcher.group(FUNCTION_NAME_GROUP))) {
        Class<?> c = EXISTED_CLASSES.get(matcher.group(FUNCTION_TYPE_GROUP).trim());
        BooleanOptional.of(Objects.nonNull(c))
            .ifTrueThen(() -> classMap.put(matcher.group(FUNCTION_NAME_GROUP).trim(), c));
      }
    }
  }

  private boolean isClassExists(String name) {
    return EXISTED_CLASSES.containsKey(name);
  }

  private Class<?> getExistedClass(String name) {
    return EXISTED_CLASSES.get(name);
  }

  private boolean isStatic(Class<?> parent, String input, Member member) {
    if (parent.getSimpleName().equals(input)) {
      return Modifier.isStatic(member.getModifiers());
    }
    return true;
  }

}
