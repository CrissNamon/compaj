package tech.hiddenproject.compaj.lang.groovy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import groovy.lang.GroovyRuntimeException;
import tech.hiddenproject.compaj.lang.CodeCheck;
import tech.hiddenproject.compaj.lang.CodeTranslation;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.data.ClassOverrider;
import tech.hiddenproject.compaj.lang.data.CodeStringData;
import tech.hiddenproject.compaj.lang.data.MethodOverrider;

public class GroovyTranslatorUtils implements TranslatorUtils {

  private final List<CodeCheck> syntaxCheckers;
  private final List<CodeTranslation> codeTranslations;
  private final Set<CodeStringData> strings;
  private boolean useRawGroovy;

  {
    syntaxCheckers = new ArrayList<>();
    codeTranslations = new ArrayList<>();
    strings = new HashSet<>();
  }

  {
    syntaxCheckers.add(this::checkExplicitMetaClassChanges);
  }

  {
    codeTranslations.add(this::translateRemoveComments);
    codeTranslations.add(this::translateGlobalMetaClassMethod);
    codeTranslations.add(this::translateNewObjects);
    codeTranslations.add(this::translateLocalMetaClassMethod);
    codeTranslations.add(this::translateDoubleSuffix);
    codeTranslations.add(this::translateOverrideMethods);
  }

  public String translate(String script) {
    if (!useRawGroovy) {
      applySyntaxCheck(script);
      script = applyTranslations(script);
    }
    return script;
  }

  public void useRawLanguage(boolean f) {
    useRawGroovy = f;
  }

  public void addCodeTranslation(CodeTranslation codeTranslation) {
    codeTranslations.add(codeTranslation);
  }

  private void applySyntaxCheck(String script) {
    for (CodeCheck codeCheck : syntaxCheckers) {
      codeCheck.check(script);
    }
  }

  private String applyTranslations(String script) {
    for (CodeTranslation codeTranslation : codeTranslations) {
      script = codeTranslation.translate(script);
      extractAllStrings(script);
    }
    return script;
  }

  private void extractAllStrings(String script) {
    Pattern p = Pattern.compile("([\\\"'])(?:(?=(\\\\?))\\2[\\s\\S])*?\\1");
    Matcher m = p.matcher(script);
    while (m.find()) {
      strings.add(new CodeStringData(m.start(), m.end() + 1));
    }
  }

  public void addSyntaxCheck(CodeCheck codeCheck) {
    syntaxCheckers.add(codeCheck);
  }

  @Override
  public boolean isLexemeInString(int start, int end) {
    return isLexemeInString(start, end, strings);
  }

  private boolean isLexemeInString(
      int start, int end, Set<CodeStringData> strings) {
    return strings.stream().anyMatch(s -> start >= s.getStart() && end <= s.getEnd());
  }

  private String translateDoubleSuffix(String script) {
    Pattern p = Pattern.compile("\\b(?<!\\!)(\\d++(?:\\.\\d+)++)(?!\\w+)");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (!isLexemeInString(m.start(), m.end())) {
        String num = m.group() + "d";
        m.appendReplacement(sb, num);
      }
    }
    m.appendTail(sb);
    script = sb.toString();
    return script;
  }

  private String translateNewObjects(String script) {
    Pattern p = Pattern.compile("(?<![:\\[])(:)(\\w++)(\\(.*\\))?(?![ ]*[{\\]])");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (!isLexemeInString(m.start(), m.end())) {
        String r = "new " + m.group().substring(1);
        if (m.group(3) == null) {
          r += "()";
        }
        m.appendReplacement(sb, r);
      }
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private String translateRemoveComments(String script) {
    script = script.replaceAll("\\/\\*[\\s\\S]*\\*\\/", "");
    return script;
  }

  private boolean checkExplicitMetaClassChanges(String script) {
    Pattern p = Pattern.compile("\\w+\\.metaClass(?:\\.\\w+)?");
    Matcher m = p.matcher(script);
    while (m.find()) {
      if (isLexemeInString(m.start(), m.end())) {
        continue;
      }
      throw new GroovyRuntimeException("Explicit MetaClass changes are not allowed!\n" + m.group());
    }
    return true;
  }

  private String translateLocalMetaClassMethod(String script) {
    Pattern p = Pattern.compile("(\\w+):::((\\w+)\\(([\\w, .]*)\\))[\\n ]*\\{");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (isLexemeInString(m.start(), m.end())) {
        continue;
      }
      checkMetaClassChangesAllowed(m.group(1));
      String r = m.group(1) + ".metaClass." + m.group(3) + " = { " + m.group(4).trim() + " -> ";
      m.appendReplacement(sb, r);
    }
    m.appendTail(sb);
    return sb.toString();
  }

  private void checkMetaClassChangesAllowed(String className) {
    if (!isMetaClassChangeAllowed(className)) {
      throw new GroovyRuntimeException("Class extensions are not allowed for: " + className);
    }
  }

  private boolean isMetaClassChangeAllowed(String className) {
    return true;
  }

  private String translateGlobalMetaClassMethod(String script) {
    Pattern p = Pattern.compile("(\\w+)::!((\\w+)\\(([\\w, .]*)\\))[\\n ]*\\{");
    Matcher m = p.matcher(script);
    String tmp = script;
    Map<String, List<MethodOverrider>> methodOverriderMap =
        new HashMap<>();
    while (m.find()) {
      if (isLexemeInString(m.start(), m.end())) {
        continue;
      }
      checkMetaClassChangesAllowed(m.group(1));
      int i = findCodeBlockEnd(m.end() + 1, script);
      String body = tmp.substring(m.end(), i);
      script = script.replace(m.group() + body, "");
      methodOverriderMap.putIfAbsent(m.group(1), new ArrayList<>());
      methodOverriderMap
          .get(m.group(1))
          .add(
              new MethodOverrider(
                  m.group(3), m.group(4), body));
    }
    StringBuilder scriptBuilder = new StringBuilder(script);
    for (Map.Entry<String, List<MethodOverrider>> entry :
        methodOverriderMap.entrySet()) {
      for (MethodOverrider methodOverrider : entry.getValue()) {
        scriptBuilder.insert(
            0,
            entry.getKey()
                + ".metaClass."
                + methodOverrider.getName()
                + " = { "
                + methodOverrider.getReturnType()
                + " -> "
                + methodOverrider.getBody()
                + "\n");
      }
    }
    script = scriptBuilder.toString();
    return script;
  }

  private int findCodeBlockEnd(int start, String script) {
    int o = 1;
    int c = 0;
    int i = start;
    while (o != c && i < script.length()) {
      if (script.charAt(i) == '}') {
        c++;
      }
      if (script.charAt(i) == '{') {
        o++;
      }
      i++;
    }
    if (o != c) {
      throw new RuntimeException("UNCLOSED BRACKETS");
    }
    return i;
  }

  private String translateOverrideMethods(String script) {
    Pattern p =
        Pattern.compile(
            "(\\w+)(\\([\\w, ]*\\))\\*\\*(\\w+)\\.(\\w+(\\([\\w, .]*\\)))(?>::(\\w+))?[\n ]*\\{");
    Matcher m = p.matcher(script);
    Map<String, ClassOverrider> extenders = new HashMap<>();
    Map<String, List<MethodOverrider>> methodOverriderMap =
        new HashMap<>();
    String tmp = script;
    while (m.find()) {
      if (isLexemeInString(m.start(), m.end())) {
        continue;
      }
      extenders.putIfAbsent(
          m.group(1),
          new ClassOverrider(
              m.group(1), m.group(3), m.group(2)));
      methodOverriderMap.putIfAbsent(m.group(1), new ArrayList<>());
      String returnType = m.group(6);
      if (returnType == null) {
        returnType = "void";
      }
      if (m.group(4).startsWith(m.group(1) + "(")) {
        returnType = "";
      }
      int i = findCodeBlockEnd(m.end() + 1, script);
      String body = tmp.substring(m.end(), i);
      extenders
          .get(m.group(1))
          .addMethodOverrider(
              new MethodOverrider(
                  m.group(4), returnType, "{" + body));
      script = script.replace(m.group() + body, "");
    }
    StringBuilder overriders = new StringBuilder();
    for (Map.Entry<String, ClassOverrider> entry :
        extenders.entrySet()) {
      overriders.append(entry.getValue().constructClass());
    }
    script = overriders.toString() + script.replaceAll("(?m)^[ \\t]*\\r?\\n", "");
    return script;
  }
}
