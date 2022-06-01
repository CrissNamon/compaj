package com.hiddenproject.compaj.core.model;

import java.util.*;
import java.util.function.*;
import com.hiddenproject.compaj.core.data.*;

public interface AgentModel<A, L extends Distance<L>> {

  void addAgent(A agent, int count);

  void addEnvironmentObject(EnvironmentObject<L> environmentObject);

  List<EnvironmentObject<L>> environmentObjects();

  void start(Function<Long, Boolean> endCondition);

  void step();

  long iterator();

  List<List<A>> getHistory();

}
