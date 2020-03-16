package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.ArrayFront;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;

import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;

/**
 * This abstract class contains methods to prepare populations before calculate an
 * indicator value.
 */
public abstract class Calculator {

  /**
   * The Maximum Reference values.
   */
  protected double[] maximumValues;
  /**
   * The Minimum Reference values.
   */
  protected double[] minimumValues;
  /**
   * The Pareto true front.
   */
  protected Front paretoTrueFront;
  /**
   * The Number of objectives.
   */
  protected int numberOfObjectives;

  /**
   * The Quality Indicator name.
   */
  protected String indicatorName;
  
  /**
   * It tells if higher values are better than lowers.
   */
  protected @Getter @Setter boolean lowerValuesAreBetter;


  protected @Getter @Setter boolean normalize;
  /**
   * Instantiates a new Calculator.
   *
   * @param numberOfObjectives the number of objectives
   */
  public Calculator(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
    paretoTrueFront = null;
    this.maximumValues = null;
    this.minimumValues = null;
    lowerValuesAreBetter=true;
    normalize=true;
  }

  /**
   * Instantiates a new Calculator.
   *
   * @param numberOfObjectives the number of objectives
   * @param paretoFront the pareto front
     * @param normalize
   */
  public Calculator(int numberOfObjectives, Front paretoFront, boolean normalize) {
    this.numberOfObjectives = numberOfObjectives;
    //to test
    //List<PointSolution> notDominatedFront=SolutionListUtils.getNondominatedSolutions(FrontUtils.convertFrontToSolutionList(paretoFront));
    //paretoFront=new ArrayFront(notDominatedFront);
    //to test
    lowerValuesAreBetter=true;
    this.normalize=normalize;
    setParetoTrueFront(paretoFront);
  }

  /**
   * Instantiates a new Calculator.
   *
   * @param numberOfObjectives the number of objectives
   * @param path the path of Pareto Front
   * @throws FileNotFoundException the file not found exception
   */
  public Calculator(int numberOfObjectives, String path) throws FileNotFoundException {
    this(numberOfObjectives, new ArrayFront(path), true);
  }

  /**
   * Add a front to pareto front. It gonna find new min and max refeverence values.
   *
   * @param front the front
   */
  public void addParetoFront(Front front) {
    double[] tempMinimum = FrontUtils.getMinimumValues(front);
    double[] tempMaximum = FrontUtils.getMaximumValues(front);

    addMinMaxReference(tempMinimum, tempMaximum);
  }

  /**
   * Update min and max reference.
   *
   * @param tempMinimum the temp minimum
   * @param tempMaximum the temp maximum
   */
  protected void addMinMaxReference(double[] tempMinimum, double[] tempMaximum) {
    // Compare min
    if (minimumValues == null) {
      minimumValues = tempMinimum;
    } else {
      for (int i = 0; i < tempMinimum.length; i++) {
        minimumValues[i] = Double.min(tempMinimum[i], minimumValues[i]);
      }
    }

    // Compare max
    if (maximumValues == null) {
      maximumValues = tempMaximum;
    } else {
      for (int i = 0; i < tempMaximum.length; i++) {
        maximumValues[i] = Double.max(tempMaximum[i], maximumValues[i]);
      }
    }
  }

  /**
   * Clear.
   */
  public void clear() {
    this.minimumValues = null;
    this.maximumValues = null;
  }

  /**
   * Execute double.
   *
   * @param frontPath the front path
   * @return the double
   * @throws FileNotFoundException the file not found exception
   */
  public double execute(String frontPath) throws FileNotFoundException {
    return calculate(new ArrayFront(frontPath), maximumValues, minimumValues);
  }

  /**
   * Execute to get indicator value double.
   *
   * @param front the front
   * @return the double
   */
  public double execute(Front front) {
    return calculate(front, maximumValues, minimumValues);
  }

  /**
   * Execute to get indicator value double.
   *
   * @param front the front
   * @return the double
   */
  public double execute(List<? extends Solution<?>> front) {
    return calculate(new ArrayFront(front), maximumValues, minimumValues);
  }

  /**
   * Execute to get indicator value double.
   *
   * @param front the front
   * @param maximumValues the maximum values
   * @param minimumValues the minimum values
   * @return the double
   */
  public double execute(List<? extends Solution<?>> front, double[] maximumValues,
      double[] minimumValues) {
    return calculate(new ArrayFront(front), maximumValues, minimumValues);
  }

  /**
   * Execute to get indicator value double.
   *
   * @param front the front
   * @param maximumValues the maximum values
   * @param minimumValues the minimum values
   * @return the double
   */
  public double execute(Front front, double[] maximumValues, double[] minimumValues) {
    return calculate(front, maximumValues, minimumValues);
  }

  /**
   * Gets pareto true front.
   *
   * @return the pareto true front
   */
  public Front getParetoTrueFront() {
    return paretoTrueFront;
  }

  /**
   * Sets pareto true front.
   *
   * @param paretoTrueFront the pareto true front
   */
  public void setParetoTrueFront(Front paretoTrueFront) {
    this.maximumValues = null;
    this.minimumValues = null;
    addParetoFront(paretoTrueFront);
    if(normalize && canNormalize()){
        FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues);
        Front normalizedFront = frontNormalizer.normalize(paretoTrueFront);
        this.paretoTrueFront = normalizedFront;
    }
    else{
        //System.err.println("Cannot normalize");
        this.paretoTrueFront = paretoTrueFront;
    }
  }
  
    private boolean canNormalize() {
        for (int j = 0; j < maximumValues.length; j++) {
            if ((maximumValues[j] - minimumValues[j]) == 0) {
                return false;
            }
        }
        return true;
    }
  
  public void setMinMax(double[] minimumValues, double[] maximumValues) {
    this.maximumValues = maximumValues;
    this.minimumValues = minimumValues;
  }
  
    protected boolean isMinMaxTheSame(double[] maximumValues, double[] minimumValues) {
        //Copied from jMetal
        for (int j = 0; j < maximumValues.length; j++) {
            if ((maximumValues[j] - minimumValues[j]) == 0) {
                return true;
            }
        }
        return false;
    }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return indicatorName;
  }

  /**
   * Execute to get indicator value double.
   *
   * @param front the front
   * @param maximumValues the maximum values
   * @param minimumValues the minimum values
   * @return the double
   */
  public abstract double calculate(Front front, double[] maximumValues, double[] minimumValues);
}
