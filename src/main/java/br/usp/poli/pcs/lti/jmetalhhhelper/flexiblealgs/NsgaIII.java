package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.PermutationTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;

/**
 *
 * @author vinicius
 */
public class NsgaIII<S extends Solution<?>> extends NSGAIII<S> implements LLHInterface<S> {

    protected OpManager selector = new OpManager();

    public NsgaIII(NSGAIIIBuilder<S> builder) {
        super(builder);
    }

    @Override
    public void run() {
        if(selector.isHHInUse()){
            initMetaheuristic();
            while (!isStoppingConditionReached()) {
                generateNewPopulation();
            }
        }
        else{
            super.run();
        }
    }

    @Override
    public int getMaxEvaluations() {
        return this.maxIterations * maxPopulationSize;
    }

    @Override
    public int getIterations() {
        return this.iterations;
    }

    @Override
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public void setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    @Override
    public void setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    @Override
    public void initMetaheuristic() {
        initMetaheuristic(createInitialPopulation());
    }

    @Override
    public void initMetaheuristic(List<S> population) {
        this.population = evaluatePopulation(population);
        initProgress();
    }

    @Override
    public void initMetaheuristic(List<S> pop, List<S> pop2) {
        initMetaheuristic(pop);
    }

    @Override
    public List<S> execute() {
        while (!isStoppingConditionReached()) {
            this.generateNewPopulation();

        }
        return population;
    }

    @Override
    public List<S> execute(List<S> inputPop, int evaluations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<S> executeMethod() {
        List<S> matingPopulation = selection(population);
        List<S> offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        return offspringPopulation;
    }

    @Override
    public int getPopulationSize() {
        return maxPopulationSize;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.maxPopulationSize = populationSize;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public void setMaxIterations(int maxIteration) {
        this.maxIterations = maxIteration;
    }

    @Override
    public void generateNewPopulation() {
        List<S> offspringPopulation = executeMethod();
        population = replacement(population, offspringPopulation);
        updateProgress();
    }

    @Override
    public OpManager getOpLLHManager() {
        return this.selector;
    }

    @Override
    public TaggedSolution entag(Solution s) {
        TaggedSolution s2;
        if(s instanceof TaggedSolution){
            return (TaggedSolution) s;
        }
        if (problem instanceof AbstractDoubleProblem) {
            s2 = new DoubleTaggedSolution((DefaultDoubleSolution) s);
        } else {
            s2 = new PermutationTaggedSolution((DefaultIntegerPermutationSolution) s);
        }
        return s2;
    }
}
