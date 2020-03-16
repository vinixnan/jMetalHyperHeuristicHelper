/* Copyright 2009-2018 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.usp.poli.pcs.lti.jmetalproblems.problems;

import br.usp.poli.pcs.lti.jmetalproblems.interfaces.RealWorldProblem;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * The Quagliarella problem.
 * <p>
 * Properties:
 * <ul>
 * <li>Disconnected Pareto set
 * <li>Convex Pareto front
 * </ul>
 * <p>
 * References:
 * <ol>
 * <li>Van Veldhuizen, D. A (1999). "Multiobjective Evolutionary Algorithms:
 * Classifications, Analyses, and New Innovations." Air Force Institute of
 * Technology, Ph.D. Thesis, Appendix B.
 * <li>Quagliarella, D., and Vicini, A. (1998). "Sub-population Policies for a
 * Parallel Multiobjective Genetic Algorithm with Applications to Wing Design."
 * In proceedings of the 1998 IEEE International Conference on Systems, Man, and
 * Cybernetics, pp. 3142-3147.
 * </ol>
 */
public class Quagliarella extends AbstractDoubleProblem implements RealWorldProblem {

    protected int qtdEvaluated = 0;

    /**
     * Constructs the Quagliarella problem.
     */
    public Quagliarella() {
        setNumberOfVariables(16);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("Quagliarella");
        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-5.12);
            upperLimit.add(5.12);
        }
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);

    }

    @Override
    public void evaluate(DoubleSolution solution) {
        qtdEvaluated++;
        double[] x = new double[getNumberOfVariables()];
        for (int i = 0; i < getNumberOfVariables(); i++) {
            x[i] = solution.getVariableValue(i);
        }
        double A1 = 0.0;
        double A2 = 0.0;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            A1 += Math.pow(x[i], 2.0) - 10.0 * Math.cos(2.0 * Math.PI * x[i]) + 10;
            A2 += Math.pow(x[i] - 1.5, 2.0)
                    - 10.0 * Math.cos(2.0 * Math.PI * (x[i] - 1.5)) + 10;
        }

        solution.setObjective(0, Math.sqrt(A1 / getNumberOfVariables()));
        solution.setObjective(1, Math.sqrt(A2 / getNumberOfVariables()));
    }

    @Override
    public boolean isConstrained() {
        return false;
    }

    @Override
    public boolean isDiscrete() {
        return false;
    }

    @Override
    public int getQtdEvaluated() {
        return qtdEvaluated;
    }

}
