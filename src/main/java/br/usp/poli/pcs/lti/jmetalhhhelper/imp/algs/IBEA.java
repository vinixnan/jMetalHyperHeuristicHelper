package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.PermutationTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Ibea;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;

/**
 *
 * @author psavd
 */
public class IBEA<S extends Solution<?>> extends Ibea<S> {

    protected OpManager selector = new OpManager();

    public IBEA(Problem problem, int populationSize, int archiveSize, int maxEvaluations, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
        super(problem, populationSize, archiveSize, maxEvaluations, selectionOperator, crossoverOperator, mutationOperator);
    }

    /**
     * Reproduction list.
     *
     * @param population the population
     * @return the list
     */
    @Override
    protected List<S> reproduction(List<S> population) {
        // Create a new offspringPopulation
        offSpringSolutionSet = new ArrayList<>(populationSize);
        S parent1;
        S parent2;
        while (offSpringSolutionSet.size() < populationSize) {
            int j = 0;
            do {
                j++;
                parent1 = selectionOperator.execute(archive);
            } while (j < org.uma.jmetal.algorithm.multiobjective.ibea.IBEA.TOURNAMENTS_ROUNDS);
            int k = 0;
            do {
                k++;
                parent2 = selectionOperator.execute(archive);
            } while (k < org.uma.jmetal.algorithm.multiobjective.ibea.IBEA.TOURNAMENTS_ROUNDS);

            List<S> parents = new ArrayList<>(2);
            parents.add(parent1);
            parents.add(parent2);

            //make the crossover
            List<S> offspring = crossoverOperator.execute(parents);
            mutationOperator.execute(offspring.get(0));
            problem.evaluate(offspring.get(0));
            //problem.evaluateConstraints(offSpring[0]);

            //New code-----------
            TaggedSolution s2;
            if (problem instanceof AbstractDoubleProblem) {
                s2 = new DoubleTaggedSolution((DefaultDoubleSolution) offspring.get(0));
            } else {
                s2 = new PermutationTaggedSolution((DefaultIntegerPermutationSolution) offspring.get(0));
            }
            selector.assignTag(parents, s2, this);
            offSpringSolutionSet.add((S) s2);
            //-----------------------------------

            evaluations++;
        }
        return offSpringSolutionSet;
    }
}
