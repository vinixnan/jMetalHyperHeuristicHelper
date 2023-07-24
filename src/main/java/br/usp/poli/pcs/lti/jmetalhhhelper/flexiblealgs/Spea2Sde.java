package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.PermutationTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.ArchivedLLHInterface;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.jmetaloriginal.SPEA2SDE;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author vinicius
 */
public class Spea2Sde<S extends Solution<?>> extends SPEA2SDE<S> implements
        ArchivedLLHInterface<S> {

    /**
     * Archive size.
     */
    protected int archiveSize;

    protected OpManager selector = new OpManager();

    /**
     * Instantiates a new Spea 2.
     *
     * @param problem the problem
     * @param maxIterations the max iterations
     * @param populationSize the population size
     * @param crossoverOperator the crossover operator
     * @param mutationOperator the mutation operator
     * @param selectionOperator the selection operator
     * @param evaluator the evaluator
     */
    public Spea2Sde(Problem<S> problem, int maxIterations, int populationSize,
            CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
            SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator);
        archiveSize = populationSize;
    }

    @Override
    public List<S> executeMethod() {
        List<S> offspringPopulation = reproduction(archive);
        offspringPopulation = evaluatePopulation(offspringPopulation);
        return offspringPopulation;
    }

    @Override
    public void generateNewPopulation() {
        selection(population);
        List<S> offspringPopulation = this.executeMethod();
        population = replacement(population, offspringPopulation);
        updateProgress();
    }

    @Override
    public List<S> execute() {
        while (!isStoppingConditionReached()) {
            generateNewPopulation();
        }
        return archive;
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
    public void setCrossoverOperator(CrossoverOperator crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    @Override
    public void setMutationOperator(MutationOperator mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    @Override
    public void initMetaheuristic() {
        this.population = new ArrayList<>();
        setPopulation(createInitialPopulation());
        setPopulation(evaluatePopulation(getPopulation()));
        selection(population);
        initProgress();
    }

    @Override
    public void initMetaheuristic(List<S> pop) {
        initMetaheuristic(new ArrayList<>(), pop);
    }

    @Override
    public void initMetaheuristic(List<S> pop, List<S> off) {
        archive = pop;
        setPopulation(off);
        selection(population);
        initProgress();
    }

    @Override
    public int getPopulationSize() {
        return this.maxPopulationSize;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.maxPopulationSize = populationSize;
    }

    @Override
    public int getMaxEvaluations() {
        return this.iterations * maxPopulationSize;
    }

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public void run() {
        this.initMetaheuristic();
        this.execute();
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return iterations >= maxIterations;
    }

    @Override
    public List<S> getArchive() {
        return this.archive;
    }

    @Override
    public void setArchive(List<S> pop) {
        this.archive = pop;
    }

    @Override
    public List<S> getPopulation() {
        return population;
    }

    @Override
    public void setPopulation(List<S> pop) {
        this.population = pop;
    }

    @Override
    public int getArchiveSize() {
        return archiveSize;
    }

    @Override
    public void setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;
    }

    @Override
    public List<S> execute(List<S> inputPop, int evaluations) {
        archive = inputPop;
        population = new ArrayList<>();
        iterations = 0;
        maxIterations = evaluations / maxPopulationSize;
        return execute();
    }

    @Override
    public void setMaxIterations(int maxIteration) {
        maxIterations = maxIteration;
    }

    @Override
    public OpManager getOpLLHManager() {
        return selector;
    }

    public List<S> updateMainPopulation(List<S> offspringPopulation) {
        population = replacement(population, offspringPopulation);
        selection(population);
        return archive;
    }

    public List<S> updateMainPopulation2(List<S> pop) {
        selection(pop);
        return archive;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offSpringPopulation = new ArrayList<>(getMaxPopulationSize());

        while (offSpringPopulation.size() < getMaxPopulationSize()) {
            List<S> parents = new ArrayList<>(2);
            S candidateFirstParent = selectionOperator.execute(population);
            parents.add(candidateFirstParent);
            S candidateSecondParent;
            candidateSecondParent = selectionOperator.execute(population);
            parents.add(candidateSecondParent);

            List<S> offspring = crossoverOperator.execute(parents);
            mutationOperator.execute(offspring.get(0));
            offSpringPopulation.add(offspring.get(0));
        }
        return offSpringPopulation;
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
