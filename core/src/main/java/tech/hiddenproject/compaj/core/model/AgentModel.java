package tech.hiddenproject.compaj.core.model;

import java.util.List;
import java.util.function.Function;

import tech.hiddenproject.compaj.core.data.Distance;
import tech.hiddenproject.compaj.core.data.EnvironmentObject;

/**
 * Represents simple agent based model.
 *
 * @param <A> Agent type
 * @param <L> Location type
 */
public interface AgentModel<A, L extends Distance<L>> {

  /**
   * Adds new agent to model.
   *
   * @param agent Agent object
   * @param count Agents count to duplicate
   */
  void addAgent(A agent, int count);

  /**
   * Adds new environment object to model.
   *
   * @param environmentObject {@link EnvironmentObject}
   */
  void addEnvironmentObject(EnvironmentObject<L> environmentObject);

  /**
   * @return Registered {@link EnvironmentObject}
   */
  List<EnvironmentObject<L>> environmentObjects();

  /**
   * Starts model until endCondition triggered
   *
   * @param endCondition {@link Function} with accepts iterations count
   */
  void start(Function<Long, Boolean> endCondition);

  /**
   * Computes next step of model.
   */
  void step();

  /**
   * @return Current iterations count
   */
  long iterator();

  /**
   * @return Model history of agents
   */
  List<List<A>> getHistory();
}
