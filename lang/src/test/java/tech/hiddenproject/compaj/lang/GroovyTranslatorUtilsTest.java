package tech.hiddenproject.compaj.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.hiddenproject.compaj.lang.groovy.GroovyTranslatorUtils;

public class GroovyTranslatorUtilsTest {

  private static final GroovyTranslatorUtils TRANSLATOR_UTILS = new GroovyTranslatorUtils();

  @Test
  public void doubleSuffixTranslationTest() {
    String script = "5.0";
    String expected = script + "d";

    String actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void newObjectTranslationTest() {
    String script = "myClass = :MyClass";
    String expected = "myClass = new MyClass()";

    String actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual);

    script = "myClass = :MyClass(1, obj)";
    expected = "myClass = new MyClass(1, obj)";
    actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual);

    script = "myClass = :tech.hiddenproject.MyClass(1, obj)";
    expected = "myClass = new tech.hiddenproject.MyClass(1, obj)";
    actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void newLocalMethodTranslationTest() {
    String script = "MyClass:::method(arg1, arg2) {}";
    String expected = "MyClass.metaClass.method = { arg1, arg2 -> }";

    String actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void newGlobalMethodTranslationTest() {
    String script = "MyClass::!method(arg1, arg2) {println 5}";
    String expected = "MyClass.metaClass.method = { arg1, arg2 -> println 5}";

    String actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual.trim());
  }

  @Test
  public void overridingMethodTranslationTest() {
    String script = "NewClass(arg1, arg2)**MyClass.newMethod(arg1, arg2)::void{ }";
    String expected = "class NewClass extends MyClass{ NewClass(arg1, arg2){ super(arg1, arg2) }\n"
        + "void newMethod(arg1, arg2){ }\n"
        + "}";

    String actual = TRANSLATOR_UTILS.translate(script);
    Assertions.assertEquals(expected, actual.trim());
  }

}
