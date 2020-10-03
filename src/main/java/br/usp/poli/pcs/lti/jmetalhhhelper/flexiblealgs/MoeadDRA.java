package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.AbstractMOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class implementing the MOEA/D-DRA algorithm described in : Q. Zhang, W. Liu,
 * and H Li, The Performance of a New Version of MOEA/D on CEC09 Unconstrained
 * MOP Test Instances, Working Report CES-491, School of CS & EE, University of
 * Essex, 02/2009
 *
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MoeadDRA extends AbstractMOEAD<DoubleSolution> implements LLHInterface<DoubleSolution> {

    protected DifferentialEvolutionCrossover differentialEvolutionCrossover;

    protected DoubleSolution[] savedValues;
    protected double[] utility;
    protected int[] frequency;
    protected int maxIterations;
    protected int iterations;
    protected double[] improvement_;

    JMetalRandom randomGenerator;

    protected OpManager selector = new OpManager();

    public MoeadDRA(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
            MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType,
            String dataDirectory, double neighborhoodSelectionProbability,
            int maximumNumberOfReplacedSolutions, int neighborSize) {
        super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
                dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
                neighborSize);

        differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;
    }

    @Override
    public void run() {
        initMetaheuristic();
        this.execute();
    }

    protected void initializePopulation() {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            DoubleSolution newSolution = (DoubleSolution) problem.createSolution();

            problem.evaluate(newSolution);
            population.add(newSolution);
            savedValues[i] = (DoubleSolution) newSolution.copy();
        }
    }

    public void utilityFunction() throws JMetalException {
        double f1, f2, uti, delta;
        for (int n = 0; n < populationSize; n++) {
            f1 = fitnessFunction(population.get(n), lambda[n]);
            f2 = fitnessFunction(savedValues[n], lambda[n]);
            delta = f2 - f1;
            if (delta > 0.001) {
                utility[n] = 1.0;
            } else {
                uti = (0.95 + (0.05 * delta / 0.001)) * utility[n];
                utility[n] = uti < 1.0 ? uti : 1.0;
            }
            savedValues[n] = (DoubleSolution) population.get(n).copy();
        }
    }

    public List<Integer> tourSelection(int depth) {
        List<Integer> selected = new ArrayList<Integer>();
        List<Integer> candidate = new ArrayList<Integer>();

        for (int k = 0; k < problem.getNumberOfObjectives(); k++) {
            // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU Et AL (NOT SORTED!!!!)
            selected.add(k);
        }

        for (int n = problem.getNumberOfObjectives(); n < populationSize; n++) {
            // set of unselected weights
            candidate.add(n);
        }

        while (selected.size() < (int) (populationSize / 5.0)) {
            int best_idd = (int) (randomGenerator.nextDouble() * candidate.size());
            int i2;
            int best_sub = candidate.get(best_idd);
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (randomGenerator.nextDouble() * candidate.size());
                s2 = candidate.get(i2);
                if (utility[s2] > utility[best_sub]) {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
            selected.add(best_sub);
            candidate.remove(best_idd);
        }
        return selected;
    }

    @Override
    public String getName() {
        return "MOEADDRA";
    }

    @Override
    public String getDescription() {
        return "Multi-Objective Evolutionary Algorithm based on Decomposition. Version with Dynamic Resource Allocation";
    }

    protected double fitnessFunction(DoubleSolution individual, double[] lambda) throws JMetalException {
        double fitness;

        if (MOEAD.FunctionType.TCHE.equals(functionType)) {
            double maxFun = -1.0e+30;

            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                double diff = Math.abs(individual.getObjective(n) - idealPoint.getValue(n));

                double feval;
                if (lambda[n] == 0) {
                    feval = 0.0001 * diff;
                } else {
                    feval = diff * lambda[n];
                }
                if (feval > maxFun) {
                    maxFun = feval;
                }
            }

            fitness = maxFun;
        } else if (MOEAD.FunctionType.AGG.equals(functionType)) {
            double sum = 0.0;
            for (int n = 0; n < problem.getNumberOfObjectives(); n++) {
                sum += (lambda[n]) * individual.getObjective(n);
            }

            fitness = sum;

        } else if (MOEAD.FunctionType.PBI.equals(functionType)) {
            double d1, d2, nl;
            double theta = 5.0;

            d1 = d2 = nl = 0.0;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d1 += (individual.getObjective(i) - idealPoint.getValue(i)) * lambda[i];
                nl += Math.pow(lambda[i], 2.0);
            }
            nl = Math.sqrt(nl);
            d1 = Math.abs(d1) / nl;

            for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
                d2 += Math.pow((individual.getObjective(i) - idealPoint.getValue(i)) - d1 * (lambda[i] / nl), 2.0);
            }
            d2 = Math.sqrt(d2);

            fitness = (d1 + theta * d2);
        } else {
            throw new JMetalException(" MOEAD.fitnessFunction: unknown type " + functionType);
        }
        return fitness;
    }

    @Override
    public int getMaxEvaluations() {
        return this.maxEvaluations;
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
    public void initMetaheuristic() {
        savedValues = new DoubleSolution[populationSize];
        utility = new double[populationSize];
        frequency = new int[populationSize];
        for (int i = 0; i < utility.length; i++) {
            utility[i] = 1.0;
            frequency[i] = 0;
        }
        improvement_ = new double[populationSize];
        randomGenerator = JMetalRandom.getInstance();
        iterations = 0;
        maxIterations = maxEvaluations / populationSize;
        initializePopulation();
        initializeUniformWeight();
        initializeNeighborhood();
        idealPoint.update(population);;
        iterations = 0;
        evaluations = populationSize;
    }

    @Override
    public void initMetaheuristic(List<DoubleSolution> pop) {
        this.population = pop;
        initializeUniformWeight();
        initializeNeighborhood();
        idealPoint.update(population);;
        iterations = 0;
        evaluations = populationSize;
    }

    @Override
    public void initMetaheuristic(List<DoubleSolution> pop, List<DoubleSolution> pop2) {
        initMetaheuristic(pop);
    }

    @Override
    public List<DoubleSolution> executeMethod() {
        int[] permutation = new int[populationSize];
        MOEADUtils.randomPermutation(permutation, populationSize);

        for (int i = 0; i < populationSize; i++) {
            int subProblemId = permutation[i];
            frequency[subProblemId]++;

            NeighborType neighborType = chooseNeighborType();
            List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

            differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
            List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

            DoubleSolution child = children.get(0);
            mutationOperator.execute(child);
            problem.evaluate(child);

            evaluations++;

            idealPoint.update(child.getObjectives());
            updateNeighborhood(child, subProblemId, neighborType);
        }
        return population;
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
        return maxIterations;
    }

    @Override
    public void setMaxIterations(int maxIteration) {
        this.maxIterations = maxIteration;
    }

    @Override
    public List<DoubleSolution> getPopulation() {
        return this.population;
    }

    @Override
    public void setPopulation(List<DoubleSolution> pop) {
        this.population = pop;
    }

    @Override
    public void generateNewPopulation() {
        Arrays.fill(improvement_, 0);
        executeMethod();
        iterations++;
        if (iterations % 30 == 0) {
            utilityFunction();
        }
    }

    @Override
    public OpManager getOpLLHManager() {
        return this.selector;
    }

    @Override
    public List<DoubleSolution> execute() {
        while (!isStoppingConditionReached()) {
            generateNewPopulation();
        }
        return (List<DoubleSolution>) population;
    }

    @Override
    public List<DoubleSolution> execute(List<DoubleSolution> inputPop, int evaluations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected boolean isStoppingConditionReached() {
        return iterations >= maxIterations || evaluations >= maxEvaluations;
    }

    @Override
    public void setCrossoverOperator(CrossoverOperator<DoubleSolution> co) {
        this.crossoverOperator = (DifferentialEvolutionCrossover) co;
    }

    @Override
    public void setMutationOperator(MutationOperator<DoubleSolution> mo) {
        this.mutationOperator = mo;
    }

    @Override
    protected void updateNeighborhood(DoubleSolution individual, int subProblemId, NeighborType neighborType) throws JMetalException {
        int size;
        int time;

        time = 0;

        if (neighborType == NeighborType.NEIGHBOR) {
            size = neighborhood[subProblemId].length;
        } else {
            size = population.size();
        }
        int[] perm = new int[size];

        MOEADUtils.randomPermutation(perm, size);

        for (int i = 0; i < size; i++) {
            int k;
            if (neighborType == NeighborType.NEIGHBOR) {
                k = neighborhood[subProblemId][perm[i]];
            } else {
                k = perm[i];
            }
            double f1, f2;

            

            f1 = fitnessFunction(population.get(k), lambda[k]);
            f2 = fitnessFunction(individual, lambda[k]);

            double fitnessimprovement = (f1 - f2) / f1;

            if (fitnessimprovement > 0) {
                population.set(k, (DoubleSolution) individual.copy());
                improvement_[subProblemId] += fitnessimprovement;
                time++;
            }

            if (time >= maximumNumberOfReplacedSolutions) {
                return;
            }
        }
    }
}
