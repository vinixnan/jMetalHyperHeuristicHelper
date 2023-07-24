package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.NsgaIII;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

/**
 *
 * @author vinicius
 */
public class NSGAIII<S extends Solution<?>> extends NsgaIII<S> {

    public NSGAIII(NSGAIIIBuilder<S> builder) {
        super(builder);
    }
    
    protected List<S> generateUsingOpManager(List<S> population) {
        selector.generateNadir(population);
        List<S> offspringPopulation = new ArrayList<>(this.getMaxPopulationSize());
        LowLevelHeuristic llh = selector.selectOp();
        CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
        MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
        int numberOfParents = crossoverOp.getNumberOfRequiredParents();
        int i=0;
        while (i < this.getMaxPopulationSize()) {
            List<S> parents = new ArrayList<>();
            for (int j = 0; j < numberOfParents; j++) {
                parents.add((S) selectionOperator.execute(population));
            }
            if (crossoverOp instanceof DifferentialEvolution) {
                ((DifferentialEvolution) crossoverOp).setCurrentSolution((DoubleSolution) population.get(i));
            }
            List<S> offspring = (List<S>) crossoverOp.execute(parents);
            for (int j = 0; j < offspring.size() && i < getMaxPopulationSize(); j++) {
                S s=offspring.get(j);
                if (mutationOp != null) {
                    mutationOp.execute(s);
                }
                //New code-----------
                TaggedSolution s2 = new DoubleTaggedSolution((DefaultDoubleSolution) s);
                problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                if (crossoverOp instanceof DifferentialEvolution) {
                    parents.add(population.get(i));
                }
                selector.assignTag(parents, s2, this);
                offspringPopulation.add((S) s2);
                i++;
            }
            selector.updateRewards(parents, offspring);
        }
        return offspringPopulation;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        if (selector.isHHInUse()) {
            List<S> ax = new ArrayList<>();
            ax.addAll(population);
            return generateUsingOpManager(ax);
        } else {
            return super.reproduction(population);
        }
    }
}
