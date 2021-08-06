package br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.OpManager;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEAD;
import org.uma.jmetal.algorithm.multiobjective.moead.MOEADD;
import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

/**
 *
 * @author vinicius
 */
public class Moeadd<S extends DoubleSolution> extends MOEADD<S> implements LLHInterface<S> {

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
        this.crossoverOperator = (CrossoverOperator<S>) (DifferentialEvolutionCrossover) co;
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
                if(position < rankIdx[curRank].length){
                    rankIdx[curRank][position] = 1;
                }
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
    
    public void updateAll(List<S> pop){
        for(S s : pop){
            idealPoint.update(s.getObjectives());
            nadirPoint.update(s.getObjectives());
            updateArchive(s);
        }
    }

    protected double fitnessFunction(S individual, double[] lambda) throws JMetalException {
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
    public void updateArchive(S indiv) {

        // find the location of 'indiv'
        setLocation(indiv, idealPoint.getValues(), nadirPoint.getValues());
        int location = (int) indiv.getAttribute("region");

        numRanks = nondominated_sorting_add(indiv);

        if (numRanks == 1) {
            deleteRankOne(indiv, location);
        } else {
            ArrayList<S> lastFront = new ArrayList<>(populationSize);
            int frontSize = countRankOnes(numRanks - 1);
            if (frontSize == 0) {  // the last non-domination level only contains 'indiv'
                frontSize++;
                lastFront.add(indiv);
            } else {
                for (int i = 0; i < populationSize; i++) {
                    if (rankIdx[numRanks - 1][i] == 1) {
                        lastFront.add((S) population.get(i));
                    }
                }
                if (((int) indiv.getAttribute(ranking.getAttributeIdentifier())) == (numRanks - 1)) {
//        if (rankSolution.getOrDefault(indiv, 0) == (numRanks - 1)) {
                    frontSize++;
                    lastFront.add(indiv);
                }
            }

            if (frontSize == 1 && lastFront.get(0).equals(indiv)) {  // the last non-domination level only has 'indiv'
                int curNC = countOnes(location);
                if (curNC > 0) {  // if the subregion of 'indiv' has other solution, drop 'indiv'
                    nondominated_sorting_delete(indiv);
                } else {  // if the subregion of 'indiv' has no solution, keep 'indiv'
                    deleteCrowdRegion1(indiv, location);
                }
            } else if (frontSize == 1 && !lastFront.get(0).equals(indiv)) { // the last non-domination level only has one solution, but not 'indiv'
                int targetIdx = findPosition(lastFront.get(0));
                int parentLocation = findRegion(targetIdx);
                int curNC = countOnes(parentLocation);
                if (parentLocation == location) {
                    curNC++;
                }

                if (curNC == 1) {  // the subregion only has the solution 'targetIdx', keep solution 'targetIdx'
                    deleteCrowdRegion2(indiv, location);
                } else {  // the subregion contains some other solutions, drop solution 'targetIdx'
                    int indivRank = (int) indiv.getAttribute(ranking.getAttributeIdentifier());
                    int targetRank = (int) population.get(targetIdx).getAttribute(ranking.getAttributeIdentifier());
                    rankIdx[targetRank][targetIdx] = 0;
                    rankIdx[indivRank][targetIdx] = 1;

                    S targetSol = population.get(targetIdx);

                    replace(targetIdx, indiv);
                    subregionIdx[parentLocation][targetIdx] = 0;
                    subregionIdx[location][targetIdx] = 1;

                    // update the non-domination level structure
                    nondominated_sorting_delete(targetSol);
                }
            } else {

                double indivFitness = fitnessFunction(indiv, lambda[location]);

                // find the index of the solution in the last non-domination level, and its corresponding subregion
                int[] idxArray = new int[frontSize];
                int[] regionArray = new int[frontSize];

                for (int i = 0; i < frontSize; i++) {
                    idxArray[i] = findPosition(lastFront.get(i));
                    if (idxArray[i] == -1) {
                        regionArray[i] = location;
                    } else {
                        regionArray[i] = findRegion(idxArray[i]);
                    }
                }

                // find the most crowded subregion, if more than one exist, keep them in 'crowdList'
                ArrayList<Integer> crowdList = new ArrayList<>();
                int crowdIdx;
                int nicheCount = countOnes(regionArray[0]);
                if (regionArray[0] == location) {
                    nicheCount++;
                }
                crowdList.add(regionArray[0]);
                for (int i = 1; i < frontSize; i++) {
                    int curSize = countOnes(regionArray[i]);
                    if (regionArray[i] == location) {
                        curSize++;
                    }
                    if (curSize > nicheCount) {
                        crowdList.clear();
                        nicheCount = curSize;
                        crowdList.add(regionArray[i]);
                    } else if (curSize == nicheCount) {
                        crowdList.add(regionArray[i]);
                    }
                }
                // find the index of the most crowded subregion
                if (crowdList.size() == 1) {
                    crowdIdx = crowdList.get(0);
                } else {
                    int listLength = crowdList.size();
                    crowdIdx = crowdList.get(0);
                    double sumFitness = sumFitness(crowdIdx);
                    if (crowdIdx == location) {
                        sumFitness = sumFitness + indivFitness;
                    }
                    for (int i = 1; i < listLength; i++) {
                        int curIdx = crowdList.get(i);
                        double curFitness = sumFitness(curIdx);
                        if (curIdx == location) {
                            curFitness = curFitness + indivFitness;
                        }
                        if (curFitness > sumFitness) {
                            crowdIdx = curIdx;
                            sumFitness = curFitness;
                        }
                    }
                }

                switch (nicheCount) {
                    case 0:
                        System.out.println("Impossible empty subregion!!!");
                        break;
                    case 1:
                        // if the subregion of each solution in the last non-domination level only has one solution, keep them all
                        deleteCrowdRegion2(indiv, location);
                        break;
                    default:
                        // delete the worst solution from the most crowded subregion in the last non-domination level
                        ArrayList<Integer> list = new ArrayList<>();
                        for (int i = 0; i < frontSize; i++) {
                            if (regionArray[i] == crowdIdx) {
                                list.add(i);
                            }
                        }
                        if (list.isEmpty()) {
                            System.out.println("Cannot happen!!!");
                        } else {
                            double maxFitness, curFitness;
                            int targetIdx = list.get(0);
                            if (idxArray[targetIdx] == -1) {
                                maxFitness = indivFitness;
                            } else {
                                maxFitness = fitnessFunction(population.get(idxArray[targetIdx]), lambda[crowdIdx]);
                            }
                            for (int i = 1; i < list.size(); i++) {
                                int curIdx = list.get(i);
                                if (idxArray[curIdx] == -1) {
                                    curFitness = indivFitness;
                                } else {
                                    curFitness = fitnessFunction(population.get(idxArray[curIdx]), lambda[crowdIdx]);
                                }
                                if (curFitness > maxFitness) {
                                    targetIdx = curIdx;
                                    maxFitness = curFitness;
                                }
                            }
                            if (idxArray[targetIdx] == -1) {
                                nondominated_sorting_delete(indiv);
                            } else {
                                //indiv.getRank();
                                int indivRank = (int) indiv.getAttribute(ranking.getAttributeIdentifier());

                                //int targetRank = ((DoubleSolution) population.get(idxArray[targetIdx])).getRank();
                                int targetRank = (int) population.get(idxArray[targetIdx]).getAttribute(ranking.getAttributeIdentifier());

                                rankIdx[targetRank][idxArray[targetIdx]] = 0;
                                rankIdx[indivRank][idxArray[targetIdx]] = 1;

                                S targetSol = population.get(idxArray[targetIdx]);

                                replace(idxArray[targetIdx], indiv);
                                subregionIdx[crowdIdx][idxArray[targetIdx]] = 0;
                                subregionIdx[location][idxArray[targetIdx]] = 1;

                                // update the non-domination level structure
                                nondominated_sorting_delete(targetSol);
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void generateNewPopulation() {
        this.executeMethod();
    }
}
