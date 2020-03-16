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
 * The Poloni problem. Van Veldhuizen observed a typo in the original paper;
 * this implementation uses Van Veldhuizen's version of the problem.
 * <p>
 * Properties:
 * <ul>
 * <li>Disconnected Pareto set
 * <li>Disconnected and convex Pareto front
 * <li>Maximization (objectives are negated)
 * </ul>
 * <p>
 * References:
 * <ol>
 * <li>Van Veldhuizen, D. A (1999). "Multiobjective Evolutionary Algorithms:
 * Classifications, Analyses, and New Innovations." Air Force Institute of
 * Technology, Ph.D. Thesis, Appendix B.
 * <li>Poloni, C., et al. (1996). "Multiobjective Optimization by GAs:
 * Application to System and Component Design." Computational Methods in Applied
 * Sciences '96: Invited Lectures and Special Technological Sessions of the
 * Third ECCOMAS Computational Fluid Dynamics Conference and the Second ECCOMAS
 * Conference on Numerical Methods in Engineering, pp. 258-264.
 * </ol>
 */
public class Poloni extends AbstractDoubleProblem implements RealWorldProblem {

    protected int qtdEvaluated = 0;

    /**
     * Constructs the Poloni problem.
     */
    public Poloni() {
        setNumberOfVariables(2);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("Poloni");
        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-Math.PI);
            upperLimit.add(Math.PI);
        }
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        qtdEvaluated++;
        double x = solution.getVariableValue(0);
        double y = solution.getVariableValue(1);
        double A1 = 0.5 * Math.sin(1.0) - 2.0 * Math.cos(1.0) + Math.sin(2.0)
                - 1.5 * Math.cos(2.0);
        double A2 = 1.5 * Math.sin(1.0) - Math.cos(1.0) + 2.0 * Math.sin(2.0)
                - 0.5 * Math.cos(2.0);
        double B1 = 0.5 * Math.sin(x) - 2.0 * Math.cos(x) + Math.sin(y)
                - 1.5 * Math.cos(y);
        double B2 = 1.5 * Math.sin(x) - Math.cos(x) + 2.0 * Math.sin(y)
                - 0.5 * Math.cos(y);
        double f1 = 1 + Math.pow(A1 - B1, 2.0) + Math.pow(A2 - B2, 2.0);
        double f2 = Math.pow(x + 3.0, 2.0) + Math.pow(y + 1.0, 2.0);

        solution.setObjective(0, f1);
        solution.setObjective(1, f2);
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
