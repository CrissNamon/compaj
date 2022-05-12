package com.hiddenproject.compaj.core.expressor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {

  private static final Map<String, ExpressionFunction> EXPRESSION_FUNCTIONS;

  private final String str;
  private int pos;
  private int ch;

  static {
    EXPRESSION_FUNCTIONS = new HashMap<>();
    EXPRESSION_FUNCTIONS.put("sin", x -> Math.sin(Math.toRadians(x[0])));
    EXPRESSION_FUNCTIONS.put("cos", x -> Math.cos(Math.toRadians(x[0])));
    EXPRESSION_FUNCTIONS.put("tan", x -> Math.tan(Math.toRadians(x[0])));
    EXPRESSION_FUNCTIONS.put("ln", x -> Math.log(x[0]));
    EXPRESSION_FUNCTIONS.put("sign", x -> Math.signum(x[0]));
    EXPRESSION_FUNCTIONS.put("sqrt", x -> Math.sqrt(x[0]));
    EXPRESSION_FUNCTIONS.put("min", x -> Math.min(x[0], x[1]));
    EXPRESSION_FUNCTIONS.put("max", x -> Math.max(x[0], x[1]));
  }

  public ExpressionParser(String s) {
    this.str = s;
    this.pos = -1;
  }

  public static void loadFunction(String name, ExpressionFunction expressionFunction) {
    EXPRESSION_FUNCTIONS.put(name, expressionFunction);
  }

  public static void loadFunction(String name, String expression) {
    EXPRESSION_FUNCTIONS.put(name, x -> {
      String formula = expression;
      Pattern pattern = Pattern.compile("(?<=[+\\-*/])*(x(?>\\(?[0-9]*\\))?)(?=[+\\-*/])*");
      Matcher matcher = pattern.matcher(formula);
      while(matcher.find()) {
        String subExpr = matcher.group();
        if(subExpr.endsWith(")")) {
          int start = subExpr.indexOf("(");
          int end = subExpr.lastIndexOf(")");
          int varNum = Integer.parseInt(subExpr.substring(start+1, end));
          formula = formula.replace(subExpr, x[varNum].toString());
        } else {
          formula = formula.replaceFirst(subExpr, x[0].toString());
        }
      }
      return ExpressionParser.eval(formula);
    });
  }

  public static double eval(final String str) {
    return new ExpressionParser(str).parse();
  }

  private void nextChar() {
    ch = (++pos < str.length()) ? str.charAt(pos) : -1;
  }

  private boolean eat(int charToEat) {
    while (ch == ' ') nextChar();
    if (ch == charToEat) {
      nextChar();
      return true;
    }
    return false;
  }

  private double parse() {
    nextChar();
    double x = parseExpression();
    if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
    return x;
  }

  private double parseExpression() {
    double x = parseTerm();
    for (; ; ) {
      if (eat('+')) x += parseTerm(); // addition
      else if (eat('-')) x -= parseTerm(); // subtraction
      else return x;
    }
  }

  private double parseTerm() {
    double x = parseFactor();
    for (; ; ) {
      if (eat('*')) x *= parseFactor(); // multiplication
      else if (eat('/')) x /= parseFactor(); // division
      else return x;
    }
  }

  private double parseFactor() {
    if (eat('+')) return +parseFactor(); // unary plus
    if (eat('-')) return -parseFactor(); // unary minus

    List<Double> nums = new ArrayList<>();
    double x;
    int startPos = this.pos;
    if (eat('(')) { // parentheses
      x = parseExpression();
      if (!eat(')')) throw new RuntimeException("Missing ')'");
    } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
      while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
      x = Double.parseDouble(str.substring(startPos, this.pos));
    } else if (ch >= 'a' && ch <= 'z') { // functions
      while (ch >= 'a' && ch <= 'z') nextChar();
      String func = str.substring(startPos, this.pos);
      if (eat('(')) {
        x = parseExpression();
        nums.add(x);
        while(eat(',')) {
          x = parseExpression();
          nums.add(x);
        }
        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
      }
      if (EXPRESSION_FUNCTIONS.containsKey(func)) {
        x = EXPRESSION_FUNCTIONS.get(func).apply(nums.toArray(new Double[0]));
      } else {
        throw new RuntimeException("Unknown function: " + func);
      }
    } else {
      throw new RuntimeException("Unexpected: " + ch);
    }
    if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
    return x;
  }
}
