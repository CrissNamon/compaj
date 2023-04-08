package tech.hiddenproject.compaj.gui.suggestion;

import java.util.Set;

public interface Suggester {

  Set<Suggestion> predict(String text, String prefix, int position);

}
