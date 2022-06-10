package tech.hiddenproject.compaj.translation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.hiddenproject.compaj.lang.TranslatorUtils;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;

public class MathTranslations {

  private static final TranslatorUtils translatorUtils;
  private static final Map<String, String> magicNumbers;

  static  {
    translatorUtils = new GroovyTranslatorUtils();
    magicNumbers = new HashMap<>();
    magicNumbers.put("%pi", String.valueOf(Math.PI));
    magicNumbers.put("%e", String.valueOf(Math.E));
  }

  public static String translateComplexNumbers(String script) {
    Pattern p = Pattern.compile("(\\d+[.\\d]*)?[ ]*([+-])?[ ]*(\\d+[.\\d]*)?%i");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (!translatorUtils.isLexemeInString(m.start(), m.end())) {
        String num = "";
        if (m.group(3) == null) {
          num = "1" + "i";
        } else {
          num = m.group(3) + "i";
        }
        if (m.group(2) == null) {
          num = "+" + num;
        } else {
          num = m.group(2) + num;
        }
        if (m.group(1) == null) {
          num = "0" + num;
        } else {
          num = m.group(1) + num;
        }
        String format = "new CompaJComplex(COMPLEX_FORMAT.parse(\"" + num + "\"))";
        m.appendReplacement(sb, format);
      }
    }
    m.appendTail(sb);
    return sb.toString();
  }

  public static String translateMagicNumbers(String script) {
    Pattern p = Pattern.compile("%[a-zA-Z]+");
    Matcher m = p.matcher(script);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (!translatorUtils.isLexemeInString(m.start(), m.end()) && magicNumbers.containsKey(m.group())) {
        m.appendReplacement(sb, magicNumbers.get(m.group()));
      }
    }
    m.appendTail(sb);
    return sb.toString();
  }

}
