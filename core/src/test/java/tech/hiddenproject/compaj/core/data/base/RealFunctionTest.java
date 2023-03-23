package tech.hiddenproject.compaj.core.data.base;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RealFunctionTest should > ")
public class RealFunctionTest {

  @Test
  @DisplayName("bind and execute function success")
  public void valueTest() {
    int expected = 5;

    RealFunction realFunction = new RealFunction("MyFunction");
    realFunction.b(x -> x[0] + x[1]);
    Assertions.assertEquals(expected, realFunction.value(3, 2));
  }

  @Test
  @DisplayName("bind single value and execute function success")
  public void singleValueTest() {
    RealFunction realFunction = new RealFunction("MyFunction");

    double expected = new Random().nextDouble();
    realFunction.b(expected);
    Assertions.assertEquals(expected, realFunction.value());
  }

}
