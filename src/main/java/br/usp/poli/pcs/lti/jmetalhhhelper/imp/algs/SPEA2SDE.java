package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.PermutationTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Spea2Sde;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author psavd
 */
public class SPEA2SDE<S extends Solution<?>> extends Spea2Sde<S> {

    public SPEA2SDE(Problem<S> problem, int maxIterations, int populationSize, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
    }

    private List<S> generateUsingOpManager(List<S> matingPopulation) {
        selector.generateNadir(matingPopulation);
        List<S> offSpringPopulation = new ArrayList<>();
        int i = 0;
        while (offSpringPopulation.size() < getPopulationSize()) {
            LowLevelHeuristic llh = selector.selectOp();
            CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
            MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
            int numberOfParents = crossoverOp.getNumberOfRequiredParents();
            List<S> parents = new ArrayList<>();
            for (int j = 0; j < numberOfParents; j++) {
                parents.add((S) selectionOperator.execute(matingPopulation));
            }
            if (crossoverOp instanceof DifferentialEvolution) {
                ((DifferentialEvolution) crossoverOp).setCurrentSolution((DoubleSolution) matingPopulation.get(i));

            }
            List<S> offspring = (List<S>) crossoverOp.execute(parents);

            for (S s : offspring) {
                if (mutationOp != null) {
                    mutationOp.execute(s);
                }

                //New code-----------
                TaggedSolution s2 = entag(s);
                problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                if (crossoverOp instanceof DifferentialEvolution) {
                    parents.add(matingPopulation.get(i));
                }
                selector.assignTag(parents, s2, this);
                offSpringPopulation.add((S) s2);
                //-----------------------------------
                if (offSpringPopulation.size() >= getMaxPopulationSize()) {
                    break;
                }
            }
            selector.updateRewards(parents, offspring);
            i++;
        }
        return offSpringPopulation;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offSpringPopulation;
        if (selector.isHHInUse()) {
            List<S> ax = new ArrayList<>();
            ax.addAll(population);
            offSpringPopulation = generateUsingOpManager(ax);
        } else {
            offSpringPopulation = new ArrayList<>(getMaxPopulationSize());

            while (offSpringPopulation.size() < getMaxPopulationSize()) {
                List<S> parents = new ArrayList<>(2);
                S candidateFirstParent = selectionOperator.execute(population);
                parents.add(candidateFirstParent);
                S candidateSecondParent;
                candidateSecondParent = selectionOperator.execute(population);
                parents.add(candidateSecondParent);

                List<S> offspring = crossoverOperator.execute(parents);
                mutationOperator.execute(offspring.get(0));

                //New code-----------
                TaggedSolution s2;
                if (problem instanceof AbstractDoubleProblem) {
                    s2 = new DoubleTaggedSolution((DefaultDoubleSolution) offspring.get(0));
                } else {
                    s2 = new PermutationTaggedSolution((DefaultIntegerPermutationSolution) offspring.get(0));
                }
                problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                selector.assignTag(parents, s2, this);
                offSpringPopulation.add((S) s2);
                //-----------------------------------
            }
        }
        return offSpringPopulation;
    }

    @Override
    public List<S> executeMethod() {
        List<S> offspringPopulation = reproduction(archive);
        //NO MORE EVALUATION HERE
        return offspringPopulation;
    }
}
