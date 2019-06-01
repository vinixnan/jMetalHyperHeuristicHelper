package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import org.uma.jmetal.solution.Solution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Gde3;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author psavd
 */
public class GDE3<S extends Solution<?>> extends Gde3<DoubleSolution> {

    protected OpManager selector = new OpManager();

    public GDE3(DoubleProblem problem, int populationSize, int maxEvaluations, DifferentialEvolutionSelection selection, DifferentialEvolutionCrossover crossover, SolutionListEvaluator evaluator) {
        super(problem, populationSize, maxEvaluations, selection, crossover, evaluator);
    }

    @Override
    protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
        List<DoubleSolution> offspringPopulation = new ArrayList<>();

        for (int i = 0; i < getMaxPopulationSize(); i++) {
            crossoverOperator.setCurrentSolution((DoubleSolution) getPopulation().get(i));
            List<DoubleSolution> parents = new ArrayList<>(3);
            for (int j = 0; j < 3; j++) {
                parents.add(matingPopulation.get(0));
                matingPopulation.remove(0);
            }

            crossoverOperator.setCurrentSolution((DoubleSolution) getPopulation().get(i));
            List<DoubleSolution> children = crossoverOperator.execute(parents);

            //New code-----------
            TaggedSolution s2 = new DoubleTaggedSolution((DefaultDoubleSolution) children.get(0));
            problem.evaluate((DoubleSolution) s2);//EVALUATION NOW IS HERE
            selector.assignTag(parents, s2, this);
            offspringPopulation.add((DoubleSolution) s2);
            //-----------------------------------
        }
        return offspringPopulation;
    }

    @Override
    public List<DoubleSolution> executeMethod() {
        this.guaranteeSize();
        List<DoubleSolution> matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        //NO EVALUATION HERE
        return offspringPopulation;
    }
}
