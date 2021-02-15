package org.uma.jmetal.problem.multiobjective.re;



import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem RE21. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class RE21 extends AbstractDoubleProblem {

  /** Constructor */
  public RE21() {
    setNumberOfVariables(4);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("RE21");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    double f = 10;
    double sigma = 10;
    double tmpVar = (f / sigma);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      upperLimit.add(3 * tmpVar);
    }

    lowerLimit.add(0, tmpVar);
    lowerLimit.add(1, Math.sqrt(2.0) * tmpVar);
    lowerLimit.add(2, Math.sqrt(2.0) * tmpVar);
    lowerLimit.add(3, tmpVar);

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
      
    double x1 = solution.getVariableValue(0);
    double x2 = solution.getVariableValue(1);
    double x3 = solution.getVariableValue(2);
    double x4 = solution.getVariableValue(3);

    double f = 10;
    double e = 200000;
    double l = 200;

    solution.setObjective(0, l * ((2 * x1) + Math.sqrt(2.0) * x2 + Math.sqrt(x3) + x4));
    solution.setObjective(
        1,
        ((f * l) / e)
            * ((2.0 / x1)
                + (2.0 * Math.sqrt(2.0) / x2)
                - (2.0 * Math.sqrt(2.0) / x3)
                + (2.0 / x4)));

  }
}
