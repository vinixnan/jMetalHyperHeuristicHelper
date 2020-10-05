package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

/**
 *
 * @author vinicius
 */
public class Moeadd<S extends DoubleSolution> extends MOEADD implements LLHInterface<S> {

    protected OpManager selector = new OpManager();

    public Moeadd(Problem problem, int populationSize, int resultPopulationSize, int maxEvaluations, CrossoverOperator crossoverOperator, MutationOperator mutation, FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability, int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossoverOperator, mutation, functionType, dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions, neighborSize);
    }

    @Override
    public void initPopulation() {
        for (int i = 0; i < populationSize; i++) {
            S newSolution = (S) problem.createSolution();
            problem.evaluate(newSolution);
            evaluations++;
            population.add(newSolution);
        }
    }

    @Override
    public void run() {
        this.initMetaheuristic();
        this.execute();
    }

    @Override
    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }

    @Override
    public int getIterations() {
        return this.evaluations / populationSize;
    }

    @Override
    public void setIterations(int iterations) {
        this.evaluations = iterations * populationSize;
    }

    @Override
    public int getPopulationSize() {
        return populationSize;
    }

    @Override
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    @Override
    public int getMaxIterations() {
        return maxEvaluations / populationSize;
    }

    @Override
    public void setMaxIterations(int maxIteration) {
        this.maxEvaluations = maxIteration * populationSize;
    }

    @Override
    public List<S> getPopulation() {
        return this.population;
    }

    @Override
    public void setPopulation(List<S> pop) {
        this.population = pop;
    }

    @Override
    public void setCrossoverOperator(CrossoverOperator<S> co) {
        this.crossoverOperator = (DifferentialEvolutionCrossover) co;
    }

    @Override
    public void setMutationOperator(MutationOperator<S> mo) {
        this.mutationOperator = mo;
    }

    @Override
    public OpManager getOpLLHManager() {
        return this.selector;
    }

    @Override
    public void initMetaheuristic() {
        population = new ArrayList<>();
        initPopulation();
        initMetaheuristic(population);
    }

    @Override
    public void initMetaheuristic(List<S> population) {
        evaluations = 0;
        neighborhood = new int[populationSize][neighborSize];
        lambda = new double[populationSize][problem.getNumberOfObjectives()];

        idealPoint = new IdealPoint(problem.getNumberOfObjectives()); // ideal point for Pareto-based population
        nadirPoint = new NadirPoint(problem.getNumberOfObjectives()); // nadir point for Pareto-based population

        rankIdx = new int[populationSize][populationSize];
        subregionIdx = new int[populationSize][populationSize];
        for (int i = 0; i < populationSize; i++) {
            subregionIdx[i][i] = 1;
        }
        subregionDist = new double[populationSize][populationSize];

        // STEP 1. Initialization
        initializeUniformWeight();
        initializeNeighborhood();

        idealPoint.update(population);
        nadirPoint.update(population);

        // initialize the distance
        for (int i = 0; i < populationSize; i++) {
            double distance = calculateDistance2(
                    population.get(i), lambda[i], idealPoint.getValues(), nadirPoint.getValues());
            subregionDist[i][i] = distance;
        }

        ranking = computeRanking(population);
        for (int curRank = 0; curRank < ranking.getNumberOfSubfronts(); curRank++) {
            List<S> front = ranking.getSubfront(curRank);
            for (S s : front) {
                int position = this.population.indexOf(s);
                rankIdx[curRank][position] = 1;
            }
        }
    }

    @Override
    public void initMetaheuristic(List<S> pop, List<S> pop2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<S> execute() {
        // main procedure
        do {
            this.generateNewPopulation();
        } while (evaluations < maxEvaluations);
        return population;
    }

    @Override
    public List<S> execute(List<S> inputPop, int evaluations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<S> executeMethod() {
        int[] permutation = new int[populationSize];
        MOEADUtils.randomPermutation(permutation, populationSize);
        for (int i = 0; i < populationSize; i++) {
            int cid = permutation[i];
            int type;
            double rnd = randomGenerator.nextDouble();

            // mating selection style
            if (rnd < neighborhoodSelectionProbability) {
                type = 1; // neighborhood
            } else {
                type = 2; // whole population
            }
            List<S> parents = matingSelection(cid, type);

            List<S> children = (List<S>) crossoverOperator.execute(parents);

            S child = children.get(0);
            mutationOperator.execute(child);
            problem.evaluate(child);

            evaluations++;

            idealPoint.update(child.getObjectives());
            nadirPoint.update(child.getObjectives());
            updateArchive(child);
        }
        return population;
    }

    @Override
    public void generateNewPopulation() {
        this.executeMethod();
    }
}
