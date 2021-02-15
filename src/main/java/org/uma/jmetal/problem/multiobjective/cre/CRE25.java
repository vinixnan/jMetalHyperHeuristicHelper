package org.uma.jmetal.problem.multiobjective.cre;

import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Class representing problem CRE25. Source: Ryoji Tanabe and Hisao Ishibuchi,
 * An easy-to-use real-world multi-objective optimization problem suite, Applied
 * Soft Computing, Vol. 89, pp. 106078 (2020). DOI:
 * https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE25 extends AbstractDoubleProblem {

    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;

    /**
     * Constructor
     */
    public CRE25() {
        setNumberOfVariables(4);
        setNumberOfObjectives(2);
        setNumberOfConstraints(1);
        setName("CRE25");

        List<Double> lowerLimit = List.of(12.0, 12.0, 12.0, 12.0);
        List<Double> upperLimit = List.of(60.0, 60.0, 60.0, 60.0);

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);

        overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>();
        numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>();
    }

    /**
     * Evaluate() method
     */
    @Override
    public void evaluate(DoubleSolution solution) {
        double x1 = Math.rint(solution.getVariableValue(0));
        double x2 = Math.rint(solution.getVariableValue(1));
        double x3 = Math.rint(solution.getVariableValue(2));
        double x4 = Math.rint(solution.getVariableValue(3));

        solution.setObjective(0, Math.abs(6.931 - ((x3 / x1) * (x4 / x2))));

        double maxValue = x1;
        if (x2 > maxValue) {
            maxValue = x2;
        }
        if (x3 > maxValue) {
            maxValue = x3;
        }
        if (x4 > maxValue) {
            maxValue = x4;
        }
        solution.setObjective(1, maxValue);

        evaluateConstraints(solution);

    }

    /**
     * EvaluateConstraints() method
     */
    public void evaluateConstraints(DoubleSolution solution) {
        double constraint = 0.5 - solution.getObjective(0) / 6.931;

        if (constraint < 0.0) {
            constraint = -constraint;
        } else {
            constraint = 0;
        }

        double overallConstraintViolation = 0.0;
        int violatedConstraints = 0;
        for (int i = 0; i < getNumberOfConstraints(); i++) {
            if (constraint < 0.0) {
                overallConstraintViolation += constraint;
                violatedConstraints++;
            }
        }

        overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
        numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }
}
