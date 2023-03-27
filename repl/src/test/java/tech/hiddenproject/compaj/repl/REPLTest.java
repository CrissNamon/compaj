package tech.hiddenproject.compaj.repl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hiddenproject.compaj.extension.CompaJComplex;

@DisplayName("REPLTest should > ")
public class REPLTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(REPLTest.class);
  private static final PrintStream STANDARD_OUT = System.out;
  private static final ByteArrayOutputStream OUTPUT_CAPTOR = new ByteArrayOutputStream();
  private static final ClassLoader CLASS_LOADER = REPLTest.class.getClassLoader();

  @BeforeAll
  public static void init() {
    System.setOut(new PrintStream(OUTPUT_CAPTOR));
    Main.init();
  }

  @AfterAll
  public static void release() {
    System.setOut(STANDARD_OUT);
  }

  @BeforeEach
  public void resetCaptor() {
    OUTPUT_CAPTOR.reset();
  }

  @Test
  @DisplayName("read input from string")
  public void inputStringTest() {
    String expected = "5";

    String script = "print " + expected;
    CompaJ.readInput(script);

    Assertions.assertEquals(expected, OUTPUT_CAPTOR.toString().trim());
  }

  @Test
  @DisplayName("read input from file")
  public void inputFileTest() {
    URL fileUrl = CLASS_LOADER.getResource("test_script_1.cjn");

    Assertions.assertNotNull(fileUrl);

    String expected = "5";

    CompaJ.readFile(fileUrl.getPath());

    Assertions.assertEquals(expected, OUTPUT_CAPTOR.toString().trim());
  }

  @Test
  @DisplayName("load CompaJComplex extension")
  public void complexExtensionTest() {
    String expected = new CompaJComplex(0, 1).toString();
    String script = "print new CompaJComplex(0, 1)";
    CompaJ.readInput(script);

    Assertions.assertEquals(expected, OUTPUT_CAPTOR.toString().trim());
  }

  @Test
  @DisplayName("load Math extension")
  public void mathExtensionTest() {
    String expected = doubleList().stream()
        .map(Math::sin)
        .collect(Collectors.toList()).toString();

    String script = "print sin(" + doubleList().toString() + ")";

    CompaJ.readInput("print \"\"");
    CompaJ.readInput(script);

    Assertions.assertEquals(expected, OUTPUT_CAPTOR.toString().trim());
  }

  private List<Double> doubleList() {
    return DoubleStream.iterate(0.0, d -> d <= 10.0, d -> d + 1)
        .boxed()
        .collect(Collectors.toList());
  }
}
