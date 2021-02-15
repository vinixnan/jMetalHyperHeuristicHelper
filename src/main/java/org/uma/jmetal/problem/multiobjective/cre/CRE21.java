package org.uma.jmetal.problem.multiobjective.cre;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.List;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Class representing problem CRE21. Source: Ryoji Tanabe and Hisao Ishibuchi,
 * An easy-to-use real-world multi-objective optimization problem suite, Applied
 * Soft Computing, Vol. 89, pp. 106078 (2020). DOI:
 * https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class CRE21 extends AbstractDoubleProblem {

    /**
     * Constructor
     */
    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;

    public CRE21() {
        setNumberOfVariables(3);
        setNumberOfObjectives(2);
        setNumberOfConstraints(3);
        setName("CRE21");

        List<Double> lowerLimit = List.of(0.01, 0.01, 0.01);
        List<Double> upperLimit = List.of(0.45, 0.10, 0.10);

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
        double x1 = solution.getVariableValue(0);
        double x2 = solution.getVariableValue(1);
        double x3 = solution.getVariableValue(2);

        solution.setObjective(0, x1 * Math.sqrt(16.0 + (x3 * x3)) + x2 * Math.sqrt(1.0 + x3 * x3));
        solution.setObjective(1, (20.0 * Math.sqrt(16.0 + (x3 * x3))) / (x1 * x3));

        evaluateConstraints(solution);

    }

    /**
     * EvaluateConstraints() method
     */
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.getNumberOfConstraints()];
        double x2, x3;

        x2 = solution.getVariableValue(1);
        x3 = solution.getVariableValue(2);

        constraint[0] = 0.1 - solution.getObjective(0);
        constraint[1] = 100000.0 - -solution.getObjective(1);
        constraint[2] = 100000 - ((80.0 * Math.sqrt(1.0 + x3 * x3)) / (x3 * x2));

        for (int i = 0; i < getNumberOfConstraints(); i++) {
            if (constraint[i] < 0.0) {
                constraint[i] = -constraint[i];
            } else {
                constraint[i] = 0;
            }
        }

        double overallConstraintViolation = 0.0;
        int violatedConstraints = 0;
        for (int i = 0; i < getNumberOfConstraints(); i++) {
            if (constraint[i] < 0.0) {
                overallConstraintViolation += constraint[i];
                violatedConstraints++;
            }
        }

        overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
        numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }
}
