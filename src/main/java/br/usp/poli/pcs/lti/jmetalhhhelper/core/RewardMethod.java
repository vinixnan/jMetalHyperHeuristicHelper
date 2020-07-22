package br.usp.poli.pcs.lti.jmetalhhhelper.core;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author vinicius
 */
public abstract class RewardMethod<S extends Solution<?>> {

    public abstract double[] calc(List<S> parents, List<S> offsprings);
    
    public double[] calc(List<S> parents, List<S> offsprings, double[] nadir){
        return calc(parents, offsprings);
    }
}
