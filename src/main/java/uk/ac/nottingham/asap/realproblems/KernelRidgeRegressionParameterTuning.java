package uk.ac.nottingham.asap.realproblems;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author psavd https://arxiv.org/abs/1303.6336
 *
 */
public class KernelRidgeRegressionParameterTuning extends ExternalProblem {

    private static final long serialVersionUID = -2817990885149134634L;

    public KernelRidgeRegressionParameterTuning() {
        setNumberOfVariables(5);
        setNumberOfObjectives(2);
        setName("KernelRidgeRegressionParameterTuning");
        setNumberOfConstraints(0);
        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0.0);
            upperLimit.add(1.0);
        }
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    @Override
    public boolean isConstrained() {
        return false;
    }

    @Override
    public boolean isDiscrete() {
        return false;
    }
}
