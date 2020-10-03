package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.mombi.MOMBI2;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author vinicius
 */
public class Mombi2<S extends Solution<?>> extends MOMBI2<S> implements LLHInterface<S> {

    protected OpManager selector = new OpManager();

    public Mombi2(Problem<S> problem, int maxIterations, CrossoverOperator<S> crossover, MutationOperator<S> mutation, SelectionOperator<List<S>, S> selection, SolutionListEvaluator<S> evaluator, String pathWeights) {
        super(problem, maxIterations, crossover, mutation, selection, evaluator, pathWeights);
    }

    @Override
    public void run() {
        if(selector.isHHInUse()){
            initMetaheuristic();
            this.execute();
        }
        else{
            super.run();
        }
            
    }

    @Override
    public int getMaxEvaluations() {
        return maxIterations * maxPopulationSize;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMutationOperator(MutationOperator<S> mutationOperator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initMetaheuristic() {
        this.initMetaheuristic(createInitialPopulation());
    }

    @Override
    public void initMetaheuristic(List<S> pop) {
        this.setPopulation(pop);
        this.evaluatePopulation(this.getPopulation());
        initProgress();
        //specific GA needed computations
        this.specificMOEAComputations();
    }

    @Override
    public void initMetaheuristic(List<S> pop, List<S> pop2) {
        this.initMetaheuristic(pop);
    }

    @Override
    public List<S> execute() {
        while (!isStoppingConditionReached()) {
            this.executeMethod();
        }
        return population;
    }

    @Override
    public List<S> execute(List<S> inputPop, int evaluations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<S> executeMethod() {
        this.generateNewPopulation();
        updateProgress();
        // specific GA needed computations
        this.specificMOEAComputations();
        return population;
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
        //this.maxIterations=maxIteration; //final
    }

    @Override
    public void generateNewPopulation() {
        List<S> matingPopulation = selection(this.getPopulation());
        List<S> offspringPopulation = reproduction(matingPopulation);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        this.setPopulation(replacement(this.getPopulation(), offspringPopulation));
    }

    @Override
    public OpManager getOpLLHManager() {
        return this.selector;
    }

}
