package uk.ac.nottingham.asap.realproblems;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author psavd source: Reference Point Based Multi-Objective Optimization
 * Using Evolutionary Algorithms http://dl.acm.org/citation.cfm?id=1144112
 * http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.420.4706&rep=rep1&type=pdf
 */
public class WeldedBeamDesign extends ExternalProblem {

    private static final long serialVersionUID = -2817990885149134634L;

    public WeldedBeamDesign() {
        setNumberOfVariables(4);
        setNumberOfObjectives(2);
        setName("WeldedBeamDesign");
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
