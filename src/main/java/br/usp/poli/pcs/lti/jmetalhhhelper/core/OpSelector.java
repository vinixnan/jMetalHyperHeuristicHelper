package br.usp.poli.pcs.lti.jmetalhhhelper.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.Solution;

/**
 * This is an abstract to define automatic LLH selection.
 */

/**
 * @param <S> jMetal needs.
 */
@SuppressWarnings("serial")
public abstract class OpSelector<S extends Solution<?>> implements Serializable {

  /**
   * Low-Level Heuristic set.
   */
  protected ArrayList<LowLevelHeuristic> llhs;

  /**
   * Default constructor.
   */
  public OpSelector() {
    this.llhs = new ArrayList<>();
  }

  /**
   * Get LowLevelHeuristic set.
   *
   * @return LowLevelHeuristic ArrayList.
   */
  public ArrayList<LowLevelHeuristic> getLlhs() {
    return llhs;
  }

  /**
   * Set LowLevelHeuristic set.
   *
   * @param llhs ArrayList.
   */
  public void setLlhs(ArrayList<LowLevelHeuristic> llhs) {
    this.llhs = llhs;
  }
  
  /**
   * Set LowLevelHeuristic set.
   *
     * @param llh
   */
  public void addLlhs(LowLevelHeuristic llh) {
    this.llhs.add(llh);
  }

  /**
   * Returns whether solutions are equals.
   *
   * @param a TaggedSolution
   * @param b TaggedSolution
   * @return if a and b are equals
   */
  public boolean equals(TaggedSolution a, TaggedSolution b) {
    for (int i = 0; i < a.getNumberOfVariables(); i++) {
      if (a.getTheVariableValue(i) != b.getTheVariableValue(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Assign Algorithm and LowLevelHeuristic to a solution considering parents, it makes
   * offspring equals to parents assign the same LowLevelHeuristic of parents.
   *
   * @param parents of solution
   * @param targetsolution target solution
   * @param appliedAlgorithm Algorithm applied
   * @return Algorithm
   */
  public LowLevelHeuristic buildTagCrossover(List<S> parents, TaggedSolution targetsolution,
      LowLevelHeuristic appliedAlgorithm) {
    for (S parent : parents) {
      //deve garantir que todos pais sejam testados
      if (parent instanceof TaggedSolution && this
          .equals((TaggedSolution) parent, targetsolution)) {
        return ((TaggedSolution) parent).getAction();
      }
    }
    return appliedAlgorithm;
  }
  
    public String getSuiteName() {
        String st = "";
        for (LowLevelHeuristic act : this.llhs) {
            st += act.toString() +" | ";
        }
        return st;
    }

  /**
   * Print Low-Level Heuristic set.
   */
  public void printSuite() {
      System.out.println(getSuiteName());
  }

  /**
   * Method to automatically selects a LowLevelHeuristic.
   *
   * @return the selected LowLevelHeuristic
   */
  public abstract LowLevelHeuristic select();
  
  public abstract void init();
  
  public abstract void updateReward(LowLevelHeuristic op, double[] reward);
  
  public abstract String getSelectHistory();
}
