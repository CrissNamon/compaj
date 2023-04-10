package tech.hiddenproject.compaj.gui.suggestion;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.trie.PatriciaTrie;
import tech.hiddenproject.aide.optional.IfTrueConditional;

/**
 * {@link Suggester} for classes, methods, fields names.
 */
public class VariableMethodsSuggester implements Suggester {

  @Override
  public Set<Suggestion> predict(CodeAnalyzer codeAnalyzer, String prefix) {
    if (prefix.contains(".")) {
      return suggest(codeAnalyzer, prefix + " ");
    }
    return new HashSet<>();
  }

  private Set<Suggestion> suggest(CodeAnalyzer codeAnalyzer, String input) {
    Set<Suggestion> suggestions = new HashSet<>();
    String[] chain = (input + " ").split("\\.");
    Class<?> c;
    for (int i = 0; i < chain.length - 1; i++) {
      String s1 = chain[i].trim();
      String s2 = chain[i + 1].trim();
      c = getClass(s1, codeAnalyzer);
      if (Objects.nonNull(c)) {
        Set<Method> methods = getMethods(c, s2, s1);
        Set<Field> fields = getFields(c, s2, s1);
        Set<Class<?>> innerClasses = getInnerClasses(c, s2);
        codeAnalyzer.addClasses(innerClasses);
        codeAnalyzer.addMethods(methods);
        codeAnalyzer.addFields(fields);
        if (i == chain.length - 2) {
          suggestions.addAll(convertMethods(methods));
          suggestions.addAll(convertFields(fields));
          suggestions.addAll(convertClasses(innerClasses));
        }
      }
    }
    return suggestions;
  }

  private Set<Suggestion> convertMethods(Set<Method> members) {
    return members.stream()
        .map(Suggestion::new)
        .collect(Collectors.toSet());
  }

  private Set<Suggestion> convertFields(Set<Field> members) {
    return members.stream()
        .map(Suggestion::new)
        .collect(Collectors.toSet());
  }

  private Set<Suggestion> convertClasses(Set<Class<?>> members) {
    return members.stream()
        .map(Suggestion::new)
        .collect(Collectors.toSet());
  }

  private Class<?> getClass(String name, CodeAnalyzer codeAnalyzer) {
    return IfTrueConditional.create()
        .ifTrue(codeAnalyzer.getVariables().containsKey(name))
        .then(() -> codeAnalyzer.getVariables().get(name))
        .ifTrue(codeAnalyzer.getClasses().containsKey(name))
        .then(() -> codeAnalyzer.getClasses().get(name))
        .ifTrue(!codeAnalyzer.getMethods().prefixMap(name).isEmpty())
        .then(() -> extractMethodReturnType(codeAnalyzer.getMethods(), name))
        .ifTrue(codeAnalyzer.getFields().containsKey(name))
        .then(() -> codeAnalyzer.getFields().get(name))
        .orElse(null);
  }

  private Class<?> extractMethodReturnType(PatriciaTrie<Class<?>> from, String name) {
    return from.prefixMap(name).values().stream().findFirst().orElse(null);
  }

  private Set<Method> getMethods(Class<?> parent, String prefix, String prevToken) {
    return Arrays.stream(parent.getDeclaredMethods())
        .filter(method -> isShouldBeCaptured(parent, method, prevToken, prefix))
        .collect(Collectors.toSet());
  }

  private Set<Field> getFields(Class<?> parent, String prefix, String prevToken) {
    return Arrays.stream(parent.getDeclaredFields())
        .filter(field -> Modifier.isPublic(field.getModifiers())
            && field.getName().startsWith(prefix)
            && isStatic(parent, prevToken, field))
        .collect(Collectors.toSet());
  }

  private Set<Class<?>> getInnerClasses(Class<?> parent, String prefix) {
    return Arrays.stream(parent.getDeclaredClasses())
        .filter(inner -> Modifier.isPublic(inner.getModifiers())
            && inner.getSimpleName().startsWith(prefix))
        .collect(Collectors.toSet());
  }

  private boolean isShouldBeCaptured(Class<?> parent, Member member, String s1, String prefix) {
    return Modifier.isPublic(member.getModifiers()) && member.getName().startsWith(prefix)
        && isStatic(parent, s1, member);
  }

  private boolean isStatic(Class<?> parent, String s1, Member member) {
    if (parent.getSimpleName().equals(s1)) {
      return Modifier.isStatic(member.getModifiers());
    }
    return !Modifier.isStatic(member.getModifiers());
  }

}
