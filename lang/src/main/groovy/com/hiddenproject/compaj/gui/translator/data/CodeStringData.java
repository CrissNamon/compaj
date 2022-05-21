package com.hiddenproject.compaj.gui.translator.data;

import java.util.Objects;

public class CodeStringData {
  private int start;
  private int end;

  public CodeStringData(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CodeStringData that = (CodeStringData) o;
    return start == that.start &&
        end == that.end;
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }
}
