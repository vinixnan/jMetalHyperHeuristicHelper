package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.MOEADReward;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Moeadd;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
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
public class MOEADD<S extends DoubleSolution> extends Moeadd<S> {

    protected double[] imp;
    
    public MOEADD(Problem problem, int populationSize, int resultPopulationSize, int maxEvaluations, CrossoverOperator crossoverOperator, MutationOperator mutation, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossoverOperator, mutation, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
        imp=new double[populationSize];
    }

    @Override
    public List<S> executeMethod() {
        if (selector.isHHInUse()) {
            boolean isMOEADRewardinUse=this.selector.getRewardmethod() instanceof MOEADReward;
            selector.generateNadir(population);
            int[] permutation = new int[populationSize];
            MOEADUtils.randomPermutation(permutation, populationSize);
            for (int i = 0; i < populationSize; i++) {
                LowLevelHeuristic llh = selector.selectOp();
                CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
                MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
                int cid = permutation[i];
                int type;
                double rnd = randomGenerator.nextDouble();
                if (rnd < neighborhoodSelectionProbability) {
                    type = 1; // neighborhood
                } else {
                    type = 2; // whole population
                }
                List<S> parents = matingSelection(cid, type);
                if (crossoverOp instanceof DifferentialEvolutionCrossover) {
                    parents.add((S) population.get(cid));
                    ((DifferentialEvolution) crossoverOp).setCurrentSolution((DoubleSolution) population.get(cid));
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
                    problem.evaluate((S) s2);//EVALUATION NOW IS HERE
                    if (crossoverOp instanceof DifferentialEvolution) {
                        parents.add((S) population.get(cid));
                    }
                    selector.assignTag(parents, s2, this);
                    evaluations++;
                    idealPoint.update(s2.getObjectives());
                    nadirPoint.update(s2.getObjectives());
                    updateArchive((S)s2);
                    if(isMOEADRewardinUse){
                        selector.updateRewards(this.calcImprovement(parents, s2, cid));
                    }
                }
                if(!isMOEADRewardinUse){
                    selector.updateRewards(parents, offspring);
                }

            }
            //selector.getSelector().updateGeneralPerformance(population);
        } else {
            super.executeMethod();
        }
        return population;
    }
    
    protected double calcImprovement(S parent,  DoubleTaggedSolution individual,  int k){
        double f1 = fitnessFunction(population.get(k), lambda[k]);
        double f2 = fitnessFunction((S)individual, lambda[k]);
        double fitnessimprovement = (f1 - f2) / f1;
        return Math.max(0, fitnessimprovement);
    }
    
    protected double calcImprovement(List<S> parents, DoubleTaggedSolution individual, int cid) {
        for (int i = 0; i < parents.size(); i++) {
            imp[cid]+=this.calcImprovement(parents.get(i), individual, cid);
        }
        return imp[cid];
    }
}
