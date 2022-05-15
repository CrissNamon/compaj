package com.hiddenproject.compaj.core.data;

public interface DataGetter<D> {
  D v(String label);
  D v(String label, int position);
}
