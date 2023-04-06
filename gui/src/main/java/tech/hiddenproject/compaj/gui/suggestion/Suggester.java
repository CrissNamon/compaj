package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Set;

public interface Suggester {

  Set<String> predict(String text, String prefix, int position);

}
