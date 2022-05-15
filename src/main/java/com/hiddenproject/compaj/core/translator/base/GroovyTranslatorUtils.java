package com.hiddenproject.compaj.core.translator.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.hiddenproject.compaj.core.translator.Translator;
import com.hiddenproject.compaj.core.translator.TranslatorUtils;
import org.junit.internal.Classes;

public class GroovyTranslatorUtils implements TranslatorUtils {

  private final static List<String> imports;
  private static boolean useRawGroovy;

  private List<StringData> strings;

  static {
    imports = new ArrayList<>();
    imports.add("import com.hiddenproject.compaj.core.CompaJ");
    imports.add("import com.hiddenproject.compaj.core.data.*");
    imports.add("import com.hiddenproject.compaj.core.data.base.*");
    imports.add("import com.hiddenproject.compaj.core.model.base.*");
    imports.add("import com.hiddenproject.compaj.core.model.*");
    imports.add("import com.hiddenproject.compaj.core.model.epidemic.*");
    imports.add("import org.apache.commons.math3.ode.nonstiff.*");
    imports.add("import org.apache.commons.math3.linear.*");
    imports.add("import static java.lang.Math.*");
    imports.add("import static com.hiddenproject.compaj.core.utils.CompajMathUtils.*");
    imports.add("import static com.hiddenproject.compaj.core.CompaJ.exit");
    imports.add("import java.util.function.Consumer");
  }

  public GroovyTranslatorUtils() {
    strings = new ArrayList<>();
  }

  public String translate(String script, Classes[] cache) {
    if(!useRawGroovy) {
      script = translateRemoveComments(script);
      updateStrings(script);
      script = translateNewObjects(script);
      updateStrings(script);
      script = translateDoubleSuffix(script);
      updateStrings(script);
      //script = translateEmptyFunctionalInterfaces(script);
      script = translateReplaceModelVar(script);
      updateStrings(script);
      script = translateOverrideMethods(script);
      script = translateAddImports(script);
      script = script
          + "\nClass klass = this.getClass().getName()\n" +
          "";
    }
    System.out.println("EVALUATING: " + script);
    return script;
  }

  public String translate(String script) {
    return translate(script, new Classes[]{});
  }

  public void startUp(Translator translator) {
    //translator.evaluate(translateAddImports(""));
  }

  public static void enableRawGroovy(boolean f) {
    useRawGroovy = f;
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
    return script;
  }

  private String translateNewObjects(String script) {
    Pattern p = Pattern.compile("(?<![:\\[])(:)(\\w++)(\\(.*\\))?(?![ ]*[{\\]])");
    Matcher m = p.matcher(script);
    int offset = 0;
    while(m.find()) {
      if(!isLexemeInString(m.start(), m.end())) {
        int start = m.start() + offset;
        int end = m.end() + offset;
        String params = m.group(3);
        if(params == null) {
          params = "()";
        }
        script = script.substring(0, start)
            + "new "
            + m.group(2)
            + params
            + script.substring(end);
        offset++;
      }
    }
    return script;
  }

  private String translateEmptyFunctionalInterfaces(String script) {
    return script.replaceAll("(?<=[\\( ])(::)(?=[\\s\\S]*\\))", "()->");
  }

  private String translateAddImports(String script) {
    return imports.stream().reduce((x, y) -> x + "\n" + y).orElse("") + "\n\n" + script;
  }

  private String translateReplaceModelVar(String script) {
    Pattern p = Pattern.compile("(?>(\\w+)(?>>)(\\w+)(?:\\(([+\\-0-9]+)\\))?)");
    Matcher m = p.matcher(script);
    while(m.find()) {
      if(!isLexemeInString(m.start(), m.end())) {
        StringBuilder statement = new StringBuilder(m.group(1))
            .append(".v(\"")
            .append(m.group(2))
            .append("\"");
        if(m.group(3) != null) {
          statement.append(", ")
              .append(m.group(3));
        }
        statement.append(")");
        script = script.replace(m.group(), statement.toString());
      }
    }
    return script;
    /*
    return script.replaceAll("(?>(\\w+)(?>\\>)(\\w+)(?!\\([0-9]+\\)))", "$1.v(\"$2\")")
        .replaceAll("(\\w+)(?>\\>)(\\w)(?>\\(([1-9]+)\\))", "$1.v(\"$2\", $3)");

     */
  }

  private String translateRemoveComments(String script) {
    return script.replaceAll("\\/\\*[\\s\\S]*\\*\\/", "");
  }

  private String translateOverrideMethods(String script) {
    Pattern p = Pattern.compile("(\\w+)(\\([\\w, ]*\\))\\*\\*(\\w+)\\.(\\w+(\\([\\w, .]*\\)))(?>::(\\w+))?[ ]*\\{");
    Matcher m = p.matcher(script);
    Map<String, ClassOverrider> extenders = new HashMap<>();
    Map<String, List<MethodOverrider>> methodOverriderMap = new HashMap<>();
    String tmp = script;
    while(m.find()) {
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
