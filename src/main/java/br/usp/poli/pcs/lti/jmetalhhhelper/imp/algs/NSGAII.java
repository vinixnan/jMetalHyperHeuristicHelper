package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Nsgaii;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.PermutationTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author psavd
 */
public class NSGAII<S extends Solution<?>> extends Nsgaii<S> {

    public NSGAII(Problem problem, int maxEvaluations, int populationSize, int matingPoolSize, int offspringPopulationSize, CrossoverOperator crossoverOperator, MutationOperator mutationOperator, SelectionOperator selectionOperator, Comparator dominanceComparator, SolutionListEvaluator evaluator) {
        super(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize, crossoverOperator, mutationOperator, selectionOperator, dominanceComparator, evaluator);
    }

    private void generateUsingOpManager(List<S> matingPopulation) {
        selector.generateNadir(matingPopulation);
        offspringPopulation = new ArrayList<>(offspringPopulationSize);
        int i=0;
        while(offspringPopulation.size() < getPopulationSize()){
            LowLevelHeuristic llh = selector.selectOp();
            CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
            MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
            int numberOfParents=crossoverOp.getNumberOfRequiredParents();
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
                TaggedSolution s2 = new DoubleTaggedSolution((DefaultDoubleSolution) s);
                problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                if (crossoverOp instanceof DifferentialEvolution) {
                    parents.add(matingPopulation.get(i));
                }
                selector.assignTag(parents, s2, this);
                offspringPopulation.add((S) s2);
                //-----------------------------------
                if (offspringPopulation.size() >= offspringPopulationSize) {
                    break;
                }
            }
            selector.updateRewards(parents, offspring);
            i++;
        }
    }
    
    

    @Override
    protected List<S> reproduction(List<S> matingPool) {
        int numberOfParents = crossoverOperator.getNumberOfRequiredParents();

        checkNumberOfParents(matingPool, numberOfParents);
        if (selector.isHHInUse()) {
            List<S> ax = new ArrayList<>();
            ax.addAll(population);
            generateUsingOpManager(ax);
        } else {
            offspringPopulation = new ArrayList<>(offspringPopulationSize);
            for (int i = 0; i < matingPool.size(); i += numberOfParents) {
                List<S> parents = new ArrayList<>(numberOfParents);
                for (int j = 0; j < numberOfParents; j++) {
                    parents.add(population.get(i + j));
                }

                List<S> offspring = crossoverOperator.execute(parents);

                for (S s : offspring) {
                    mutationOperator.execute(s);
                    //New code-----------
                    TaggedSolution s2=entag(s);
                    problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                    selector.assignTag(parents, s2, this);
                    offspringPopulation.add((S) s2);
                    //-----------------------------------
                    if (offspringPopulation.size() >= offspringPopulationSize) {
                        break;
                    }
                }
            }
        }
        return offspringPopulation;
    }

    @Override
    public List<S> executeMethod() {
        List<S> matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        //evaluation removed here
        population = replacement(population, offspringPopulation);
        updateProgress();
        return population;
    }
}
