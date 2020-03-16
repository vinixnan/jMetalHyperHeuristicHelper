package br.usp.poli.pcs.lti.jmetalproblems.problems;

import br.usp.poli.pcs.lti.jmetalproblems.interfaces.RealWorldProblem;
import java.io.FileNotFoundException;
import org.uma.jmetal.problem.multiobjective.ebes.Ebes;
import org.uma.jmetal.solution.DoubleSolution;

/**
 *
 * @author vinicius
 */
public class EbesReal extends Ebes implements RealWorldProblem {

    protected int qtdEvaluated = 0;

    public EbesReal() throws FileNotFoundException {
    }

    public EbesReal(String ebesFileName, String[] objectiveList) throws FileNotFoundException {
        super(ebesFileName, objectiveList);
    }

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
