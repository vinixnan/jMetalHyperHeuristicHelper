/**
 * @author wzl The Vehicle crashworthiness (VC) surrogate problem instances are from the paper:
 * Multiobjective optimization for crash safety design of vehicles using stepwise regression model
 * by Xingtao Liao; Qing Li; Xujing Yang; Weigang Zhang; Wei Li.
 * VC1 is to minimise three objectives: 1. Weight of the vehicle; 2. collision acceleration between t1 = 0.05 s and t2 = 0.07 s in the full frontal crash scenario.
 * 3. Toe board instrusion in the offset-frontal crash scenario.
 */
package br.usp.poli.pcs.lti.jmetalproblems.problems.VC;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import br.usp.poli.pcs.lti.jmetalproblems.interfaces.RealWorldProblem;

public class VC1 extends AbstractDoubleProblem implements RealWorldProblem{
    protected int qtdEvaluated;
    private static final long serialVersionUID = -6291831458123882892L;

    /*Defaultly construct a problem instance with 5 variables, 3 objectives, 0 constraints. Each variable indicates the thickness of each structure member
	 *  around the frontal structure. Each variable is in [1,3] mm.
     */
    public VC1() throws ClassNotFoundException {
        this(5, 3);
    }

    public VC1(Integer numberOfVariables, Integer numberOfObjectives) throws ClassNotFoundException {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setNumberOfConstraints(0);
        setName("VC1");
        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(1.0);
            upperLimit.add(3.0);
        }
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
        qtdEvaluated=0;
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        // TODO Auto-generated method stub
        qtdEvaluated++;
        double[] x = new double[getNumberOfVariables()];
        for (int i = 0; i < getNumberOfVariables(); i++) {
            x[i] = solution.getVariableValue(i);
        }
        double[] f = new double[getNumberOfObjectives()];

        //1st objective
        f[0] = 1640.2823 + 2.3573285 * x[0] + 2.3220035 * x[1] + 4.5688768 * x[2] + 7.7213633 * x[3] + 4.4559504 * x[4];
        //2nd objective
        f[1] = 6.5856 + 1.15 * x[0] - 1.0427 * x[1] + 0.9738 * x[2] + 0.8364 * x[3] - 0.3695 * x[0] * x[3] + 0.0861 * x[0] * x[4]
                + 0.3628 * x[1] * x[3] - 0.1106 * x[0] * x[0] - 0.3437 * x[2] * x[2] + 0.1764 * x[3] * x[3];
        //3rd objective
        f[2] = -0.0551 + 0.0181 * x[0] + 0.1024 * x[1] + 0.0421 * x[2] - 0.0073 * x[0] * x[1] + 0.024 * x[1] * x[2] - 0.0118 * x[1] * x[3]
                - 0.0204 * x[2] * x[3] - 0.008 * x[2] * x[4] - 0.0241 * x[1] * x[1] + 0.0109 * x[3] * x[3];

        for (int i = 0; i < getNumberOfObjectives(); i++) {
            solution.setObjective(i, f[i]);
        }
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
