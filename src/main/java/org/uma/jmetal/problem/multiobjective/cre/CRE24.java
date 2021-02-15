package org.uma.jmetal.problem.multiobjective.cre;

import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

/**
 * Class representing problem CRE24. Source: Ryoji Tanabe and Hisao Ishibuchi,
 * An easy-to-use real-world multi-objective optimization problem suite, Applied
 * Soft Computing, Vol. 89, pp. 106078 (2020). DOI:
 * https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE24 extends AbstractDoubleProblem {

    public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;
    public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints;

    /**
     * Constructor
     */
    public CRE24() {
        setNumberOfVariables(7);
        setNumberOfObjectives(2);
        setNumberOfConstraints(11);
        setName("CRE24");

        List<Double> lowerLimit = List.of(2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0);
        List<Double> upperLimit = List.of(3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5);

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
        double x3 = Math.rint(solution.getVariableValue(2));
        double x4 = solution.getVariableValue(3);
        double x5 = solution.getVariableValue(4);
        double x6 = solution.getVariableValue(5);
        double x7 = solution.getVariableValue(6);

        solution.setObjective(0, 0.7854 * x1 * (x2 * x2) * (((10.0 * x3 * x3) / 3.0) + (14.933 * x3) - 43.0934)
                - 1.508 * x1 * (x6 * x6 + x7 * x7)
                + 7.477 * (x6 * x6 * x6 + x7 * x7 * x7)
                + 0.7854 * (x4 * x6 * x6 + x5 * x7 * x7));

        double tmpVar = Math.pow((745.0 * x4) / (x2 * x3), 2.0) + 1.69 * 1e7;
        solution.setObjective(1, Math.sqrt(tmpVar) / (0.1 * x6 * x6 * x6));

        evaluateConstraints(solution);

    }

    /**
     * EvaluateConstraints() method
     */
    public void evaluateConstraints(DoubleSolution solution) {
        double[] constraint = new double[this.getNumberOfConstraints()];

        double x1 = solution.getVariableValue(0);
        double x2 = solution.getVariableValue(1);
        double x3 = Math.rint(solution.getVariableValue(2));
        double x4 = solution.getVariableValue(3);
        double x5 = solution.getVariableValue(4);
        double x6 = solution.getVariableValue(5);
        double x7 = solution.getVariableValue(6);

        constraint[0] = -(1.0 / (x1 * x2 * x2 * x3)) + 1.0 / 27.0;
        constraint[1] = -(1.0 / (x1 * x2 * x2 * x3 * x3)) + 1.0 / 397.5;
        constraint[2] = -(x4 * x4 * x4) / (x2 * x3 * x6 * x6 * x6 * x6) + 1.0 / 1.93;
        constraint[3] = -(x5 * x5 * x5) / (x2 * x3 * x7 * x7 * x7 * x7) + 1.0 / 1.93;
        constraint[4] = -(x2 * x3) + 40.0;
        constraint[5] = -(x1 / x2) + 12.0;
        constraint[6] = -5.0 + (x1 / x2);
        constraint[7] = -1.9 + x4 - 1.5 * x6;
        constraint[8] = -1.9 + x5 - 1.1 * x7;
        constraint[9] = -solution.getObjective(1) + 1300.0;

        double tmpVar = Math.pow((745.0 * x5) / (x2 * x3), 2.0) + 1.575 * 1e8;
        constraint[10] = -Math.sqrt(tmpVar) / (0.1 * x7 * x7 * x7) + 1100.0;

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
