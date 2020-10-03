package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.MoeadDRA;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

/**
 *
 * @author vinicius
 */
public class MOEADDRA extends MoeadDRA {

    protected boolean useFIR;

    public MOEADDRA(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations, MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, mutation, crossover, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
        useFIR = false;
    }

    @Override
    public List<DoubleSolution> executeMethod() {
        if (selector.isHHInUse()) {
            selector.generateNadir(population);
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);

            for (int i = 0; i < populationSize; i++) {
                int subProblemId = permutation[i];

                LowLevelHeuristic llh = selector.selectOp();
                CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
                MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
                if (crossoverOp instanceof DifferentialEvolutionCrossover) {
                    ((DifferentialEvolution) crossoverOp).setCurrentSolution(population.get(subProblemId));
                }
                frequency[subProblemId]++;

                NeighborType neighborType = chooseNeighborType();
                List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);
                if (!(crossoverOp instanceof DifferentialEvolutionCrossover)) {
                    SecureRandom sr = new SecureRandom();
                    parents.remove(sr.nextInt(parents.size()));
                }
                List<DoubleSolution> child = (List<DoubleSolution>) crossoverOp.execute(parents);
                List<DoubleSolution> offspring = new ArrayList<>();
                offspring.add(child.get(0));//because of NR 

                for (DoubleSolution s : offspring) {
                    if (mutationOp != null) {
                        mutationOp.execute(s);
                    }
                    //New code-----------
                    DoubleTaggedSolution s2 = new DoubleTaggedSolution((DefaultDoubleSolution) s);
                    problem.evaluate((DoubleSolution) s2);//EVALUATION NOW IS HERE
                    if (crossoverOp instanceof DifferentialEvolution) {
                        parents.add(population.get(subProblemId));
                    }
                    selector.assignTag(parents, s2, this);
                    evaluations++;
                    idealPoint.update(s2.getObjectives());
                    updateNeighborhood(s2, subProblemId, neighborType);
                    //improvement_[cur_id]
                }
                if (useFIR) {
                    selector.updateRewards(improvement_[subProblemId]);
                } else {
                    selector.updateRewards(parents, offspring);
                }

            }
        } else {
            super.executeMethod();
        }
        return population;
    }

    public boolean isUseFIR() {
        return useFIR;
    }

    public void setUseFIR(boolean useFIR) {
        this.useFIR = useFIR;
    }

}
