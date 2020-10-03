package br.usp.poli.pcs.lti.jmetalhhhelper.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.Solution;

/**
 * This class manages a LLH for meta-heuristics. It uses OpSelector to select a
 * LLH and sets to crossoverOperator and mutationOperator.
 */
/**
 * @param <S> jMetal needs.
 */
public class OpManager<S extends Solution<?>> implements Serializable {

    /**
     * an OpSelector used to automatically selects LLHs.
     */
    protected OpSelector selector;

    /**
     * the current selected LowLevelHeuristic.
     */
    protected LowLevelHeuristic llh;

    /**
     * the current crossover operator, obtained from LowLevelHeuristic.
     */
    protected CrossoverOperator<S> crossoverOperator;

    /**
     * the current mutation operator, obtained from LowLevelHeuristic.
     */
    protected MutationOperator<S> mutationOperator;

    protected RewardMethod rewardmethod;

    protected double[] nadir;

    public OpManager() {
        rewardmethod = null;
        selector = null;
        nadir = null;
    }

    /**
     * Default constructor.
     *
     * @param selector
     * @param rewardmethod
     */
    public OpManager(OpSelector selector, RewardMethod rewardmethod) {
        this.selector = selector;
        this.rewardmethod = rewardmethod;
        nadir = null;
    }

    /**
     * Get automatic selector.
     *
     * @return OpSelector selector.
     */
    public OpSelector getSelector() {
        return selector;
    }

    /**
     * Set automatic selector.
     *
     * @param selector (automatic).
     */
    public void setSelector(OpSelector selector) {
        this.selector = selector;
    }

    /**
     * Selects a LowLevelHeuristic using the instantiated selector.
     *
     * @return the selected LowLevelHeuristic.
     */
    public LowLevelHeuristic selectOp() {
        if (isHHInUse()) {
            llh = this.selector.select();
            crossoverOperator = (CrossoverOperator<S>) llh.getCrossover().getOp();
            mutationOperator = (MutationOperator<S>) llh.getMutation().getOp();
            return llh;
        }
        this.llh = null;
        return null;
    }
    
    public boolean isHHInUse(){
        return this.selector != null;
    }

    /**
     * Discover Algorithm and LowLevelHeuristic to a solution if the selector is
     * instantiated.
     *
     * @param parents of solution.
     * @param targetsolution target solution.
     * @param appliedAlgorithm Algorithm applied.
     */
    public void assignTag(List<S> parents, TaggedSolution targetsolution,
            Algorithm appliedAlgorithm) {
        if (this.selector != null) {
            targetsolution.setAction(this.selector.buildTagCrossover(parents, targetsolution, llh));
        }
        targetsolution.setAlgorithm(this.buildTagAlg(parents, targetsolution, appliedAlgorithm));
    }

    /**
     * Assign Algorithm and LowLevelHeuristic to a solution considering parents,
     * it makes offspring equals to parents assign the same LowLevelHeuristic of
     * parents.
     *
     * @param parents of solution
     * @param targetsolution target solution
     * @param appliedAlgorithm Algorithm applied
     * @return Algorithm
     */
    protected Algorithm buildTagAlg(List<S> parents, TaggedSolution targetsolution,
            Algorithm appliedAlgorithm) {
        for (Solution<?> parent : parents) {
            //deve garantir que todos pais sejam testados
            if (parent instanceof TaggedSolution) {
                if (((TaggedSolution) parent).strVariables().equals(targetsolution.strVariables())) {
                    return ((TaggedSolution) parent).getAlgorithm();
                }
            } else {
                int qtd = 0;
                for (int i = 0; i < targetsolution.getNumberOfVariables(); i++) {
                    if (parent.getVariableValue(i) == targetsolution.getTheVariableValue(i)) {
                        qtd++;
                    }
                }
                if (qtd == targetsolution.getNumberOfVariables()) {
                    return null;
                }
            }
        }
        return appliedAlgorithm;
    }

    /**
     * Gets Crossover operator.
     *
     * @return CrossoverOperator
     */
    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    /**
     * Set Crossover operator.
     *
     * @param crossoverOperator crossover.
     */
    public void setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    /**
     * Get Mutation operator.
     *
     * @return MutationOperator mutation.
     */
    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    /**
     * Set Mutation operator.
     *
     * @param mutationOperator mutation.
     */
    public void setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    public RewardMethod getRewardmethod() {
        return rewardmethod;
    }

    public void setRewardmethod(RewardMethod rewardmethod) {
        this.rewardmethod = rewardmethod;
    }

    public void updateRewards(List<S> parents, List<S> offsprings) {
        for(S p: parents){
            List<S> onlyone=new ArrayList<>();
            onlyone.add(p);
            double[] reward = rewardmethod.calc(onlyone, offsprings, nadir);
            this.selector.updateReward(llh, reward);
        }
        
    }
    
    public void updateRewards(double reward) {
        double[] rewards=new double[1];
        rewards[0]=reward;
        this.selector.updateReward(llh, rewards);
    }

    public void generateNadir(List<S> population) {
        if (nadir == null) {
            nadir = new double[population.get(0).getNumberOfObjectives()];
            population.forEach(s -> {
                for (int i = 0; i < nadir.length; i++) {
                    nadir[i] = Math.max(s.getObjective(i), nadir[i]);
                }
            });
            for (int i = 0; i < nadir.length; i++) {
                nadir[i] = nadir[i] * 2.0;
            }
        }

    }
}
