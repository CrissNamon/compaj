package com.hiddenproject.compaj.core.translator.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hiddenproject.compaj.core.translator.CodeCheck;
import com.hiddenproject.compaj.core.translator.CodeTranslation;
import com.hiddenproject.compaj.core.translator.TranslatorUtils;
import groovy.lang.GroovyRuntimeException;

public class GroovyTranslatorUtils implements TranslatorUtils {

  private final static List<String> metaClassChanges;
  private boolean useRawGroovy;

  private List<CodeCheck> syntaxCheckers;
  private List<CodeTranslation> codeTranslations;
  private List<StringData> strings;

  {
    syntaxCheckers = new ArrayList<>();
    codeTranslations = new ArrayList<>();
    strings = new ArrayList<>();
  }

  {
    syntaxCheckers.add(this::isMetaClassChangeAllowed);
  }

  {
    codeTranslations.add(this::translateRemoveComments);
    codeTranslations.add(this::translateAddMetaClassChanges);
    codeTranslations.add(this::translateGlobalMetaClassMethod);
    codeTranslations.add(this::translateNewObjects);
    codeTranslations.add(this::translateLocalMetaClassMethod);
    codeTranslations.add(this::translateDoubleSuffix);
    codeTranslations.add(this::translateReplaceModelVar);
    codeTranslations.add(this::translateOverrideMethods);
  }

  static {
    metaClassChanges = new ArrayList<>();
    //metaClassChanges.add("Double.metaClass.plus = { Variable v -> v.g() + doubleValue() }");
    metaClassChanges.add("Double.metaClass.minus = { Variable v -> v.g() - doubleValue() }");
    metaClassChanges.add("Double.metaClass.div = { Variable v -> v.g() / doubleValue() }");
    metaClassChanges.add("Double.metaClass.multiply = { Variable v -> v.g() * doubleValue() }");
    metaClassChanges.add("Integer.metaClass.plus = { Variable v -> v.g() + doubleValue() }");
    metaClassChanges.add("Integer.metaClass.minus = { Variable v -> v.g() - doubleValue() }");
    metaClassChanges.add("Integer.metaClass.div = { Variable v -> v.g() / doubleValue() }");
    metaClassChanges.add("Integer.metaClass.multiply = { Variable v -> v.g() * doubleValue() }");
  }

  public String translate(String script) {
    if(!useRawGroovy) {
      applySyntaxCheck(script);
      script = applyTranslations(script);
    }
   // System.out.println("EVALUATING: " + script);
    return script;
  }

  public void useRawGroovy(boolean f) {
    useRawGroovy = f;
  }

  public boolean isUseRawGroovy() {
    return useRawGroovy;
  }

  private String applyTranslations(String script) {
    for(CodeTranslation codeTranslation : codeTranslations) {
      script = codeTranslation.translate(script);
    }
    return script;
  }

  private void applySyntaxCheck(String script) {
    for(CodeCheck codeCheck : syntaxCheckers) {
      codeCheck.check(script);
    }
  }

  private List<StringData> getAllStrings(String script) {
    List<StringData> result = new ArrayList<>();
    Pattern p = Pattern.compile("([\\\"'])(?:(?=(\\\\?))\\2[\\s\\S])*?\\1");
    Matcher m = p.matcher(script);
    while(m.find()) {
      result.add(new StringData(m.start(), m.end()+1));
    }
    return result;
  }

  private boolean isLexemeInString(int start, int end, List<StringData> strings) {
    return strings.stream().anyMatch(s -> start >= s.getStart() && end <= s.getEnd());
  }

  private boolean isLexemeInString(int start, int end) {
    return isLexemeInString(start, end, strings);
  }

  private String translateDoubleSuffix(String script) {
    Pattern p = Pattern.compile("\\b(?<!\\!)(\\d++(?:\\.\\d+)++)(?!\\w+)");
    Matcher m = p.matcher(script);
    int offset = 0;
    while(m.find()) {
      if(!isLexemeInString(m.start(), m.end())) {
        int start = m.start() + offset;
        int end = m.end() + offset;
        script = script.substring(0, start)
            + m.group()
            + "d"
            + script.substring(end);
        offset++;
      }
    }
    updateStrings(script);
    return script;
  }

  private String translateNewObjects(String script) {
    Pattern p = Pattern.compile("(?<![:\\[])(:)(\\w++)(\\(.*\\))?(?![ ]*[{\\]])");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while(m.find()) {
      if(!isLexemeInString(m.start(), m.end())) {
        String r = "new " + m.group().substring(1);
        m.appendReplacement(sb, r);
      }
    }
    m.appendTail(sb);
    updateStrings(script);
    return sb.toString();
  }

  private String translateAddMetaClassChanges(String script) {
    return metaClassChanges.stream().reduce((x, y) -> x + "\n" + y).orElse("") + "\n\n" + script;
  }

  private String translateReplaceModelVar(String script) {
    Pattern p = Pattern.compile("(?>(\\w+)[ ]*(?>>)[\\s]*(\\w+)(?:\\(([+\\-0-9]+)\\))?)");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while(m.find()) {
      if(!isLexemeInString(m.start(), m.end())) {
        String r = m.group(1)
            + ".v(\""
            + m.group(2)
            + "\"";
        if (m.group(3) != null) {
          r += ", "
              + m.group(3);
        }
        r += ")";
        m.appendReplacement(sb, r);
      }
    }
    m.appendTail(sb);
    updateStrings(script);
    return sb.toString();
  }

  private String translateRemoveComments(String script) {
    script =  script.replaceAll("\\/\\*[\\s\\S]*\\*\\/", "");
    updateStrings(script);
    return script;
  }

  private boolean isMetaClassChangeAllowed(String className) {
    return true;
  }

  private void checkExplicitMetaClassChanges(String script) {
    Pattern p = Pattern.compile("\\w+\\.metaClass(?:\\.\\w+)?");
    Matcher m = p.matcher(script);
    while(m.find()) {
      if(isLexemeInString(m.start(), m.end())) {
        continue;
      }
      throw new GroovyRuntimeException("Explicit MetaClass changes are not allowed!\n" + m.group());
    }
  }

  private String translateLocalMetaClassMethod(String script) {
    Pattern p = Pattern.compile("(\\w+):::((\\w+)\\(([\\w, .]*)\\))[\\n ]*\\{");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while(m.find()) {
      if(isLexemeInString(m.start(), m.end())) {
        continue;
      }
      if(!isMetaClassChangeAllowed(m.group(1))) {
        throw new GroovyRuntimeException("Extensions for class " + m.group(1) + " is now allowed");
      }
      String r = m.group(1) + ".metaClass." + m.group(3)
          + " = { " + m.group(4).trim()
          + " -> ";
      m.appendReplacement(sb, r);
    }
    m.appendTail(sb);
    updateStrings(script);
    return sb.toString();
  }

  private String translateGlobalMetaClassMethod(String script) {
    Pattern p = Pattern.compile("(\\w+)::!((\\w+)\\(([\\w, .]*)\\))[\\n ]*\\{");
    Matcher m = p.matcher(script);
    String tmp = script;
    Map<String, List<MethodOverrider>> methodOverriderMap = new HashMap<>();
    while(m.find()) {
      if(isLexemeInString(m.start(), m.end())) {
        continue;
      }
      if(!isMetaClassChangeAllowed(m.group(1))) {
        throw new GroovyRuntimeException("Extensions for class " + m.group(1) + " is now allowed");
      }
      int o = 1;
      int c = 0;
      int i = m.end()+1;
      while(o!=c && i < tmp.length()) {
        if(tmp.charAt(i) == '}') c++;
        if(tmp.charAt(i) == '{') o++;
        i++;
      }
      String body = "";
      if(i<=tmp.length()) {
        body = tmp.substring(m.end(), i);
      }else{
        throw new RuntimeException("UNCLOSED BREAKETS");
      }
      script = script.replace(m.group() + body, "");
      methodOverriderMap.putIfAbsent(m.group(1), new ArrayList<>());
      methodOverriderMap.get(m.group(1)).add(new MethodOverrider(m.group(3), m.group(4), body));
    }
    for(Map.Entry<String, List<MethodOverrider>> entry : methodOverriderMap.entrySet()) {
      for(MethodOverrider methodOverrider : entry.getValue()) {
        script = entry.getKey() + ".metaClass." + methodOverrider.name + " = { "
            + methodOverrider.getReturnType() + " -> "
            + methodOverrider.getBody() + "\n" + script;
      }
    }
    updateStrings(script);
    return script;
  }

  private String translateOverrideMethods(String script) {
    Pattern p = Pattern.compile("(\\w+)(\\([\\w, ]*\\))\\*\\*(\\w+)\\.(\\w+(\\([\\w, .]*\\)))(?>::(\\w+))?[\n ]*\\{");
    Matcher m = p.matcher(script);
    Map<String, ClassOverrider> extenders = new HashMap<>();
    Map<String, List<MethodOverrider>> methodOverriderMap = new HashMap<>();
    String tmp = script;
    while(m.find()) {
      if(isLexemeInString(m.start(), m.end())) {
        continue;
      }
      extenders.putIfAbsent(m.group(1), new ClassOverrider(m.group(1), m.group(3), m.group(2)));
      methodOverriderMap.putIfAbsent(m.group(1), new ArrayList<>());
      String returnType = m.group(6);
      if(returnType == null) {
        returnType = "void";
      }
      if(m.group(4).startsWith(m.group(1) + "(")) {
        returnType = "";
      }
      int o = 1;
      int c = 0;
      int i = m.end()+1;
      while(o!=c && i < tmp.length()) {
        if(tmp.charAt(i) == '}') c++;
        if(tmp.charAt(i) == '{') o++;
        i++;
      }
      String body = "";
      if(i<=tmp.length()) {
        body = tmp.substring(m.end(), i);
      }else{
        throw new RuntimeException("UNCLOSED BREAKETS");
      }
      methodOverriderMap.get(m.group(1)).add(new MethodOverrider(m.group(4), returnType, "{" + body));
      script = script.replace(m.group() + body, "");
    }
    StringBuilder overriders = new StringBuilder();
    for(Map.Entry<String, ClassOverrider> entry : extenders.entrySet()) {
      overriders.append(constructClass(entry.getKey(), entry.getValue().getBase(), entry.getValue().getConstructor(), methodOverriderMap.get(entry.getKey())));
    }
    script = overriders.toString() + script.replaceAll("(?m)^[ \\t]*\\r?\\n", "");
    updateStrings(script);
    return script;
  }

  private String constructClass(String name, String base, String constructor, List<MethodOverrider> overriders) {
    StringBuilder c = new StringBuilder("class ")
        .append(name)
        .append(" extends ")
        .append(base)
        .append("{ ")
        .append(name)
        .append(constructor)
        .append("{ super")
        .append(constructor)
        .append(" }\n");
    for(MethodOverrider methodOverrider : overriders) {
      c.append(constructMethod(methodOverrider));
      c.append("\n");
    }
    c.append("}\n");
    return c.toString();
  }

  private String constructMethod(MethodOverrider methodOverrider) {
    return methodOverrider.getReturnType() + " " + methodOverrider.getName() + methodOverrider.getBody();
  }

  private String constructConstructor(String className, MethodOverrider methodOverrider) {
    return className + " " + methodOverrider.getBody();
  }

  private void updateStrings(String script) {
    this.strings = getAllStrings(script);
  }

  private class ClassOverrider {
    private String name;
    private String base;
    private String constructor;

    public ClassOverrider(String name, String base, String constructor) {
      this.name = name;
      this.base = base;
      this.constructor = constructor;
    }

    public String getName() {
      return name;
    }

    public String getBase() {
      return base;
    }

    public String getConstructor() {
      return constructor;
    }
  }

  private class MethodOverrider {
    private String name;
    private String returnType;
    private String body;

    public MethodOverrider(String name, String returnType, String body) {
      this.name = name;
      this.returnType = returnType;
      this.body = body;
    }

    public String getName() {
      return name;
    }

    public String getReturnType() {
      return returnType;
    }

    public String getBody() {
      return body;
    }
  }

  private class StringData {
    private int start;
    private int end;

    public StringData(int start, int end) {
      this.start = start;
      this.end = end;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }
  }
}
