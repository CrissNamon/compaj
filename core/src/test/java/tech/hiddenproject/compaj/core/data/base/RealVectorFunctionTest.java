package tech.hiddenproject.compaj.core.data.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RealVectorFunctionTest should > ")
public class RealVectorFunctionTest {

  @Test
  @DisplayName("bind and execute function success")
  public void valueTest() {
    Double[] expected = new Double[]{0d, 1d};

    RealVectorFunction realFunction = new RealVectorFunction("MyFunction");
    realFunction.b(x -> x);

    Assertions.assertEquals(expected, realFunction.value(expected));
  }

  @Test
  @DisplayName("bind single value and execute function success")
  public void singleValueTest() {
    Double[] expected = new Double[]{0d, 1d};

    RealVectorFunction realFunction = new RealVectorFunction("MyFunction");
    realFunction.b(expected);

    Assertions.assertEquals(expected, realFunction.value());
  }

}
