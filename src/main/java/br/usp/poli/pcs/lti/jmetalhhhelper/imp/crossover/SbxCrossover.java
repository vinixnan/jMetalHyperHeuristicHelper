package br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover;

import java.util.HashMap;
import java.util.Map;

import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * The type Sbx crossover.
 * This class extends the jMetal crossover class and implements Operator
 * interface. This was         created to use polymorphism without changes jMetal crossover
 * class.
 */
@SuppressWarnings("serial")
public class SbxCrossover extends SBXCrossover implements
    br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.Operator {

  /**
   * HashMap which contains all operator parameters.
   */
  protected Map<String, Object> parameters;

  /**
   * Default constructor.
   */
  public SbxCrossover() {
    super(0, 0);
    this.parameters = new HashMap<>();
  }
  
  public SbxCrossover(double crossoverProbability, double distributionIndex, RandomGenerator<Double> randomGenerator) {
    super(crossoverProbability, distributionIndex, randomGenerator) ;
    this.parameters = new HashMap<>();
  }

  /**
   * Instantiates a new Sbx crossover.
   *
   * @param crossoverProbability the crossover probability
   * @param distributionIndex the distribution index
   */
  public SbxCrossover(double crossoverProbability, double distributionIndex) {
    super(crossoverProbability, distributionIndex);
    this.parameters = new HashMap<>();
  }

  @Override
  public void allocateParameters() {
    this.setDistributionIndex((double) this.parameters.get("distributionIndex"));
    this.setCrossoverProbability((double) this.parameters.get("probability"));
  }

  @Override
  public void setParameter(String attributeName, Object value) {
    this.parameters.put(attributeName, value);
  }

  @Override
  public Object getParameter(String attributeName) {
    return this.parameters.getOrDefault(attributeName, null);
  }

  @Override
  public Map<String, Object> getParameters() {
    return parameters;
  }

  @Override
  public void setParameters(Map<String, Object> parameters) {
    this.parameters = parameters;
  }
}
