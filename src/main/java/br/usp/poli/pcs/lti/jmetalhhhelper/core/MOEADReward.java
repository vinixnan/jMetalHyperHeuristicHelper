package br.usp.poli.pcs.lti.jmetalhhhelper.core;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *
 * @author vinicius
 */
public class MOEADReward<S extends Solution<?>> extends RewardMethod<S>{

    @Override
    public double[] calc(List<S> parents, List<S> offsprings) {
        //this class exists just for keep standards
        return new double[0];
    }
    
}
