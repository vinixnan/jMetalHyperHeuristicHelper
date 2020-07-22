package br.usp.poli.pcs.lti.jmetalhhhelper.imp.algs;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.DoubleTaggedSolution;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.LowLevelHeuristic;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.TaggedSolution;
import org.uma.jmetal.solution.Solution;
import br.usp.poli.pcs.lti.jmetalhhhelper.flexiblealgs.Gde3;
import br.usp.poli.pcs.lti.jmetalhhhelper.imp.crossover.DifferentialEvolution;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 *
 * @author psavd
 */
public class GDE3<S extends Solution<?>> extends Gde3<DoubleSolution> {
    
    public GDE3(DoubleProblem problem, int populationSize, int maxEvaluations, DifferentialEvolutionSelection selection, DifferentialEvolutionCrossover crossover, SolutionListEvaluator evaluator) {
        super(problem, populationSize, maxEvaluations, selection, crossover, evaluator);
    }
    


    private void generateUsingOpManager(List<DoubleSolution> matingPopulation) {
        selector.generateNadir(matingPopulation);
        offspringPopulation = new ArrayList<>();
        int i=0;
        //System.out.println("Generation "+this.iterations);
        while(offspringPopulation.size() < getMaxPopulationSize()){
            LowLevelHeuristic llh = selector.selectOp();
            CrossoverOperator crossoverOp = (CrossoverOperator) llh.getCrossover().getOp();
            MutationOperator mutationOp = (MutationOperator) llh.getMutation().getOp();
            int numberOfParents=crossoverOp.getNumberOfRequiredParents();
            if (crossoverOp instanceof DifferentialEvolution) {
                ((DifferentialEvolution) crossoverOp).setCurrentSolution((DoubleSolution) getPopulation().get(i));
            }
            List<S> parents = new ArrayList<>();
            while (parents.size() < numberOfParents) {
                parents.addAll((Collection<? extends S>) selectionOperator.execute(matingPopulation));
            }
            if (crossoverOp instanceof DifferentialEvolution) {
                ((DifferentialEvolution) crossoverOp).setCurrentSolution((DoubleSolution) getPopulation().get(i));
            }
            else{
                Collections.shuffle(parents);
                parents.remove(0);
            }
            List<DoubleSolution> offspring = (List<DoubleSolution>) crossoverOp.execute(parents);
            //List<DoubleSolution> offspring=new ArrayList<>();
            //offspring.add(offsprings.get(0));//warranty 1
            
            for (DoubleSolution s : offspring) {
                if (mutationOp != null) {
                    mutationOp.execute(s);
                }
                //New code-----------
                TaggedSolution s2 = new DoubleTaggedSolution((DefaultDoubleSolution) s);
                problem.evaluate((DoubleSolution) s2);//EVALUATION NOW IS HERE
                if (crossoverOp instanceof DifferentialEvolution) {
                    parents.add((S) matingPopulation.get(i));
                }
                selector.assignTag(parents, s2, this);
                offspringPopulation.add((DoubleSolution) s2);
                //-----------------------------------
                if (offspringPopulation.size() >= getMaxPopulationSize()) {
                    break;
                }
            }
            selector.updateRewards(parents, offspring);
            i++;
        }
    }

    @Override
    protected List<DoubleSolution> reproduction(List<DoubleSolution> matingPopulation) {
        if (selector.isHHInUse()) {
            generateUsingOpManager(matingPopulation);
        } else {
            List<DoubleSolution> offspringPopulation = new ArrayList<>();
            for (int i = 0; i < getMaxPopulationSize(); i++) {
                crossoverOperator.setCurrentSolution((DoubleSolution) getPopulation().get(i));
                List<DoubleSolution> parents = new ArrayList<>(3);
                for (int j = 0; j < 3; j++) {
                    parents.add(matingPopulation.get(0));
                    matingPopulation.remove(0);
                }

                crossoverOperator.setCurrentSolution((DoubleSolution) getPopulation().get(i));
                List<DoubleSolution> children = crossoverOperator.execute(parents);

                //New code-----------
                TaggedSolution s2 = new DoubleTaggedSolution((DefaultDoubleSolution) children.get(0));
                problem.evaluate((DoubleSolution) s2);//EVALUATION NOW IS HERE
                selector.assignTag(parents, s2, this);
                offspringPopulation.add((DoubleSolution) s2);
                //-----------------------------------
            }
        }
        return offspringPopulation;
    }

    @Override
    public List<DoubleSolution> executeMethod() {
        this.guaranteeSize();
        List<DoubleSolution> matingPopulation = selection(population);
        offspringPopulation = reproduction(matingPopulation);
        //NO EVALUATION HERE
        return offspringPopulation;
    }
}
