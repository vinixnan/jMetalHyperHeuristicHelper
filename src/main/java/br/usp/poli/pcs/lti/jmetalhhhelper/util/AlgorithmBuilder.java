package br.usp.poli.pcs.lti.jmetalhhhelper.util;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.ParametersforAlgorithm;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.ParametersforHeuristics;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.Operator;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.BlxAlphaCrossover;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.HuxCrossover;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.NullCross;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.OnePointCrossover;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.PmxCrossover;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.SbxCrossover;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.mutation.BitFlipMuta;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.mutation.NonUniformMuta;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.mutation.NullMuta;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.mutation.PermutationSwapMuta;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.mutation.PolynomialMuta;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.mutation.UniformMuta;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMException;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.GDE3;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.IBEA;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.MOEADD;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.MOEADDRA;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.MOMBI2;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.NSGAII;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.NSGAIII;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.SPEA2;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs.mIBEA;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

/**
 * This class builds algorithms.
 */
/**
 * The type Algorithm builder.
 *
 * @param <S> jMetal need.
 */
public class AlgorithmBuilder<S extends Solution<?>> {

    /**
     * The Problem.
     */
    protected Problem problem;

    protected RandomGenerator<Double> randomGenerator;

    /**
     * Instantiates a new Algorithm builder.
     *
     * @param problem the problem
     * @param randomGenerator
     */
    public AlgorithmBuilder(Problem problem, RandomGenerator<Double> randomGenerator) {
        this.problem = problem;
        this.randomGenerator = randomGenerator;
    }

    public AlgorithmBuilder(Problem problem) {
        this.problem = problem;
        this.randomGenerator = null;
    }

    /**
     * Generate cross crossover operator.
     *
     * @param configParams the config params
     * @return the crossover operator
     * @throws JMException the jm exception
     */
    public CrossoverOperator generateCross(ParametersforHeuristics configParams) throws JMException {
        Operator operator = null;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("probability", configParams.getCrossoverProbality());
        if ("sbxCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            parameters.put("distributionIndex", configParams.getCrossoverDistribution());
            if (randomGenerator != null) {
                operator = new SbxCrossover(configParams.getCrossoverProbality(), configParams.getCrossoverDistribution(), randomGenerator);
            } else {
                operator = new SbxCrossover(configParams.getCrossoverProbality(), configParams.getCrossoverDistribution());
            }
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("blxAlphaCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            parameters.put("alpha", configParams.getAlpha());
            operator = new BlxAlphaCrossover();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("huxCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            operator = new HuxCrossover();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("onePointCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            operator = new OnePointCrossover();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("NullCross".equalsIgnoreCase(configParams.getCrossoverName())) {
            operator = new NullCross();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("DifferentialEvolutionCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            parameters.put("cr", configParams.getDeCr());
            parameters.put("f", configParams.getDeF());
            parameters.put("k", configParams.getDeK());
            parameters.put("variant", configParams.getDeVariant());
            if (randomGenerator != null) {
                operator = new DifferentialEvolution(0, 0, configParams.getDeVariant(), randomGenerator);
            } else {
                operator = new DifferentialEvolution(0, 0, configParams.getDeVariant());
            }
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("pmxCrossover".equalsIgnoreCase(configParams.getCrossoverName())) {
            operator = new PmxCrossover();
            operator.setParameters(parameters);
            operator.allocateParameters();

        }
        return (CrossoverOperator) operator;
    }

    public Comparator generateComparator() {
        return new DominanceComparator<>();
    }

    /**
     * Generate muta mutation operator.
     *
     * @param configParams the config params
     * @param maxIterations the max iterations
     * @return the mutation operator
     * @throws JMException the jm exception
     */
    public MutationOperator generateMuta(ParametersforHeuristics configParams, int maxIterations)
            throws JMException {
        Operator operator = null;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("probability", configParams.getMutationProbability());
        if ("polynomialMutation".equalsIgnoreCase(configParams.getMutationName())) {
            parameters.put("distributionIndex", configParams.getMutationDistribution());
            if (randomGenerator != null) {
                operator = new PolynomialMuta(configParams.getMutationProbability(), configParams.getMutationDistribution(), randomGenerator);
            } else {
                operator = new PolynomialMuta(configParams.getMutationProbability(), configParams.getMutationDistribution());
            }
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("uniformMutation".equalsIgnoreCase(configParams.getMutationName())) {
            parameters.put("perturbation", configParams.getMutationPertubation());
            operator = new UniformMuta();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("nonUniformMutation".equalsIgnoreCase(configParams.getMutationName())) {
            parameters.put("perturbation", configParams.getMutationPertubation());
            parameters.put("maxIterations", maxIterations);
            operator = new NonUniformMuta();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("bitFlipMutation".equalsIgnoreCase(configParams.getMutationName())) {
            operator = new BitFlipMuta();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("nullMutation".equalsIgnoreCase(configParams.getMutationName())) {
            operator = new NullMuta();
            operator.setParameters(parameters);
            operator.allocateParameters();

        } else if ("PermutationSwapMutation".equalsIgnoreCase(configParams.getMutationName())) {
            operator = new PermutationSwapMuta();
            operator.setParameters(parameters);
            operator.allocateParameters();

        }
        return (MutationOperator) operator;
    }

    /**
     * Generate selection selection operator.
     *
     * @return the selection operator
     * @throws JMException the jm exception
     */
    public SelectionOperator generateSelection() throws JMException {
        SelectionOperator se = new BinaryTournamentSelection();
        return se;
    }

    /**
     * Create ibea standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createMIbea(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new mIBEA(problem, configAlg.getPopulationSize(),
                configAlg.getArchiveSize(),
                configAlg.getMaxIteractions() * configAlg.getPopulationSize(),
                selection, crossover, mutation);
        return algorithm;
    }

    /**
     * Create ibea standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createIbea(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new IBEA(problem, configAlg.getPopulationSize(),
                configAlg.getArchiveSize(),
                configAlg.getMaxIteractions() * configAlg.getPopulationSize(),
                selection, crossover, mutation);
        return algorithm;
    }

    /**
     * Create nsga-ii standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createNsgaii(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        Comparator comparator = this.generateComparator();

        int matingPoolSize = configAlg.getPopulationSize();
        int offspringPopulationSize = configAlg.getPopulationSize();

        LLHInterface algorithm = new NSGAII(problem,
                configAlg.getMaxIteractions() * configAlg.getPopulationSize(),
                configAlg.getPopulationSize(), matingPoolSize, offspringPopulationSize, crossover,
                mutation, selection, comparator, new SequentialSolutionListEvaluator());
        return algorithm;
    }

    /**
     * Create spea 2 standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createSpea2(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = this.generateSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        int k = 1;
        LLHInterface algorithm = new SPEA2(problem, configAlg.getMaxIteractions(),
                configAlg.getPopulationSize(), crossover,
                mutation, selection, new SequentialSolutionListEvaluator(), k);
        return algorithm;
    }

    /**
     * Create spea 2 standard meta-heuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard meta-heuristic
     * @throws JMException the jm exception
     */
    public LLHInterface createGde3(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException {
        configHeuristic.setDeCr(0.2);//TEMP @TODO new parameter file
        configHeuristic.setDeF(0.2);
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        LLHInterface algorithm = new GDE3((DoubleProblem) problem,
                configAlg.getPopulationSize(), configAlg.getMaxIteractions()
                * configAlg.getPopulationSize(), new DifferentialEvolutionSelection(),
                (DifferentialEvolutionCrossover) crossover,
                new SequentialSolutionListEvaluator());
        return algorithm;
    }

    protected LLHInterface createMoead(ParametersforAlgorithm configAlg, ParametersforHeuristics configHeuristic) throws JMException {
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        if (!(crossover instanceof DifferentialEvolutionCrossover)) { //protect from HH methods
            crossover = new DifferentialEvolutionCrossover();
        }
        LLHInterface algorithm = new MOEADDRA(problem, configAlg.getPopulationSize(), configAlg.getPopulationSize(), configAlg.getMaxEvaluations(), mutation, crossover, configAlg.getMoeadFunction(), configAlg.getWeightsPath(), configAlg.getNeighborhoodSelectionProbability(), configAlg.getMaximumNumberOfReplacedSolutions(), configAlg.getNeighborSize());
        return algorithm;
    }

    protected LLHInterface createMoeadD(ParametersforAlgorithm configAlg, ParametersforHeuristics configHeuristic) throws JMException {
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new MOEADD(problem, configAlg.getPopulationSize(), configAlg.getPopulationSize(), configAlg.getMaxEvaluations(), crossover, mutation, configAlg.getMoeadFunction(), configAlg.getWeightsPath(), configAlg.getNeighborhoodSelectionProbability(), configAlg.getMaximumNumberOfReplacedSolutions(), configAlg.getNeighborSize());
        return algorithm;
    }

    protected LLHInterface createMombiII(ParametersforAlgorithm configAlg, ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        LLHInterface algorithm = new MOMBI2(problem, configAlg.getMaxIteractions(), crossover, mutation, selection, new SequentialSolutionListEvaluator(), configAlg.getWeightsPath());
        return algorithm;
    }

    protected LLHInterface createNsgaIII(ParametersforAlgorithm configAlg, ParametersforHeuristics configHeuristic) throws JMException {
        SelectionOperator selection = new RandomSelection();
        CrossoverOperator crossover = this.generateCross(configHeuristic);
        MutationOperator mutation = this.generateMuta(configHeuristic, configAlg.getMaxIteractions());
        NSGAIIIBuilder builder = new NSGAIIIBuilder(problem);
        builder.setMutationOperator(mutation);
        builder.setSelectionOperator(selection);
        builder.setCrossoverOperator(crossover);
        builder.setMaxIterations(configAlg.getMaxIteractions());
        builder.setPopulationSize(configAlg.getPopulationSize());
        builder.setSolutionListEvaluator(new SequentialSolutionListEvaluator());
        LLHInterface algorithm = new NSGAIII(builder);
        return algorithm;
    }

    /**
     * Create standard metaheuristic.
     *
     * @param configAlg the config alg
     * @param configHeuristic the config heuristic
     * @return the standard metaheuristic
     * @throws JMException the jm exception
     * @throws FileNotFoundException the file not found exception
     */
    public LLHInterface create(ParametersforAlgorithm configAlg,
            ParametersforHeuristics configHeuristic) throws JMException, FileNotFoundException {
        switch (configAlg.getAlgorithmName()) {
            case "Ibea":
                return createIbea(configAlg, configHeuristic);
            case "mIbea":
                return createMIbea(configAlg, configHeuristic);
            case "Nsgaii":
                return this.createNsgaii(configAlg, configHeuristic);
            case "Spea2":
                return createSpea2(configAlg, configHeuristic);
            case "Gde3":
                return createGde3(configAlg, configHeuristic);
            case "MoeadDra":
                return createMoead(configAlg, configHeuristic);
            case "MoeaDD":
                return createMoeadD(configAlg, configHeuristic);
            case "Mombi2":
                return createMombiII(configAlg, configHeuristic);
            case "Nsgaiii":
                return createNsgaIII(configAlg, configHeuristic);
            default:
                System.err.println("Algorithm not found");
                return null;
        }
    }
}
