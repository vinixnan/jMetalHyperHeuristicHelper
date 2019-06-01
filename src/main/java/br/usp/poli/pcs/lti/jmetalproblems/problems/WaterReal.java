package br.usp.poli.pcs.lti.jmetalproblems.problems;

import br.usp.poli.pcs.lti.jmetalproblems.interfaces.RealWorldProblem;
import org.uma.jmetal.problem.multiobjective.Water;

/**
 *
 * @author vinicius
 */
public class WaterReal extends Water implements RealWorldProblem{
    @Override
    public boolean isConstrained() {
        return true;
    }

    @Override
    public boolean isDiscrete() {
        return false;
    }
}
