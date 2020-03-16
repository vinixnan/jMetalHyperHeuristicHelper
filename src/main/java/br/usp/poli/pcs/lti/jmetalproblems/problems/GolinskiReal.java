package br.usp.poli.pcs.lti.jmetalproblems.problems;

import br.usp.poli.pcs.lti.jmetalproblems.interfaces.RealWorldProblem;
import org.uma.jmetal.problem.multiobjective.Golinski;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author vinicius
 */
public class GolinskiReal extends Golinski implements RealWorldProblem {

    protected int qtdEvaluated = 0;

    @Override
    public boolean isConstrained() {
        return true;
    }

    @Override
    public boolean isDiscrete() {
        return false;
    }

    @Override
    public int getQtdEvaluated() {
        return qtdEvaluated;
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        qtdEvaluated++;
        super.evaluate(solution);
    }
}
