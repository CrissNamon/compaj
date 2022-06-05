package tech.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.function.Function;
import tech.hiddenproject.compaj.core.data.Distance;
import tech.hiddenproject.compaj.core.data.EnvironmentObject;

public interface AgentModel<A, L extends Distance<L>> {

  void addAgent(A agent, int count);

  void addEnvironmentObject(EnvironmentObject<L> environmentObject);

  List<EnvironmentObject<L>> environmentObjects();

  void start(Function<Long, Boolean> endCondition);

  void step();

  long iterator();

  List<List<A>> getHistory();
}
