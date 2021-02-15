package br.usp.poli.pcs.lti.jmetalhhhelper.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.Solution;

/**
 * This is an abstract to define automatic LLH selection.
 */
/**
 * @param <S> jMetal needs.
 */
@SuppressWarnings("serial")
public abstract class OpSelector<S extends Solution<?>> implements Serializable {

    /**
     * Low-Level Heuristic set.
     */
    protected ArrayList<LowLevelHeuristic> llhs;

    protected ArrayList<Boolean> active;
    protected int counter;
    protected int[] strikes;
    protected int maxgen;
    protected int maxstrike;

    /**
     * Default constructor.
     *
     * @param maxgen
     * @param maxstrike
     */
    public OpSelector(int maxgen, int maxstrike) {
        this.llhs = new ArrayList<>();
        this.active = new ArrayList<>();
        this.maxgen = maxgen;
        this.maxstrike = maxstrike;
        counter = 0;
    }

    /**
     * Get LowLevelHeuristic set.
     *
     * @return LowLevelHeuristic ArrayList.
     */
    public ArrayList<LowLevelHeuristic> getLlhs() {
        return llhs;
    }

    /**
     * Set LowLevelHeuristic set.
     *
     * @param llhs ArrayList.
     */
    public void setLlhs(ArrayList<LowLevelHeuristic> llhs) {
        this.llhs = llhs;
        this.active = new ArrayList<>();
        for (LowLevelHeuristic llh : llhs) {
            active.add(true);
        }
        this.strikes = new int[llhs.size()];
        counter = 0;
    }

    /**
     * Set LowLevelHeuristic set.
     *
     * @param llh
     */
    public void addLlhs(LowLevelHeuristic llh) {
        this.llhs.add(llh);
        active.add(true);
    }

    /**
     * Returns whether solutions are equals.
     *
     * @param a TaggedSolution
     * @param b TaggedSolution
     * @return if a and b are equals
     */
    public boolean equals(TaggedSolution a, TaggedSolution b) {
        for (int i = 0; i < a.getNumberOfVariables(); i++) {
            if (a.getTheVariableValue(i) != b.getTheVariableValue(i)) {
                return false;
            }
        }
        return true;
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
    public LowLevelHeuristic buildTagCrossover(List<S> parents, TaggedSolution targetsolution,
            LowLevelHeuristic appliedAlgorithm) {
        for (S parent : parents) {
            //deve garantir que todos pais sejam testados
            if (parent instanceof DoubleTaggedSolution && this
                    .equals((DoubleTaggedSolution) parent, targetsolution)) {
                return ((DoubleTaggedSolution) parent).getAction();
            }
        }
        return appliedAlgorithm;
    }

    public String getSuiteName() {
        String st = "";
        for (LowLevelHeuristic act : this.llhs) {
            st += act.toString() + " | ";
        }
        return st;
    }

    /**
     * Print Low-Level Heuristic set.
     */
    public void printSuite() {
        System.out.println(getSuiteName());
    }

    protected Map<String, List<TaggedSolution>> splitPopulation(List<S> population) {
        Map<String, List<TaggedSolution>> separatedSolutionSet = new HashMap<>();
        for (int i = 0; i < llhs.size(); i++) {
            separatedSolutionSet.put(llhs.get(i).getName(), new ArrayList<>());
        }
        for (int i = 0; i < population.size(); i++) {
            Solution solution = population.get(i);
            if (solution instanceof TaggedSolution) {
                TaggedSolution ts = (TaggedSolution) solution;
                if (ts.getAction() != null) {
                    separatedSolutionSet.get(ts.getAction().getName()).add(ts);
                }
            }
        }
        return separatedSolutionSet;
    }

    protected int howManyActive() {
        int qtd = 0;
        for (boolean b : active) {
            if (b) {
                qtd++;
            }
        }
        return qtd;
    }

    public void updateGeneralPerformance(List<S> population) {

        if (howManyActive() > 1) {
            counter++;
            //descobre o pior valor entre os ativos
            Map<String, List<TaggedSolution>> separatedSolutionSet = this.splitPopulation(population);
            int smaller = Integer.MAX_VALUE;
            int pos = -1;
            int i = 0;
            for (LowLevelHeuristic llh : llhs) {
                int qtd = separatedSolutionSet.get(llh.getName()).size();
                if (active.get(i)) {
                    if (qtd < smaller) {
                        smaller = qtd;
                        pos = i;
                    }
                }
                i++;
            }
            strikes[pos]++;
            if (strikes[pos] > maxstrike) {
                active.set(pos, false);
                //System.out.println(llhs.get(pos).getName() + " banido");
                for (int j = 0; j < strikes.length; j++) {
                    if (strikes[j] < maxstrike) {
                        strikes[j] = 0;
                    }
                }
            } else {
                //System.out.println(llhs.get(pos).getName() + " penalizado");
            }
        } else if (this.counter > 1) {
            this.counter--;
            //System.out.println("Only one");
        } else {
            //System.out.println("RESET------------");
            this.counter = 0;
            Arrays.fill(this.strikes, 0);
            for (int j = 0; j < active.size(); j++) {
                active.set(j, true);
            }
        }
        //System.out.println(this.howManyActive());
    }

    /*
    public void updateGeneralPerformance(List<S> population) {
        counter++;
        if (counter == maxgen && howManyActive() > 1) {
            counter = 0;
            //descobre o pior valor entre os ativos
            Map<String, List<TaggedSolution>> separatedSolutionSet = this.splitPopulation(population);
            int smaller = Integer.MAX_VALUE;
            int pos = -1;
            int i = 0;
            for (LowLevelHeuristic llh : llhs) {
                int qtd = separatedSolutionSet.get(llh.getName()).size();
                if (active.get(i)) {
                    if (qtd < smaller) {
                        smaller = qtd;
                        pos = i;
                    }
                }
                i++;
            }

            //ativa todos que nao atingiram o maximo permitido
            for (int j = 0; j < strikes.length; j++) {
                if (strikes[j] <= maxstrike) {
                    active.set(j, true);
                } else {
                    active.set(j, false);
                }
            }

            //Atribui valor e stike ao pior
            active.set(pos, false);
            strikes[pos]++;
            if (strikes[pos] > maxstrike) {
                System.out.println(llhs.get(pos).getName() + " banido");
                for (int j = 0; j < strikes.length; j++) {
                    if (strikes[j] < maxstrike) {
                        strikes[j]=0;
                    }
                }
            } else {
                System.out.println(llhs.get(pos).getName() + " penalizado");
            }
            System.out.println(this.howManyActive());
        }
    }
     */
    /**
     * Method to automatically selects a LowLevelHeuristic.
     *
     * @return the selected LowLevelHeuristic
     */
    public abstract LowLevelHeuristic select();

    public abstract void init();

    public abstract void updateReward(LowLevelHeuristic op, double[] reward);

    public abstract String getSelectHistory();
}
