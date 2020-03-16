package uk.ac.nottingham.asap.realproblems;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

/**
 *
 * @author vinicius
 */
public class StardardBase {
    protected AbstractDoubleProblem problem;

    public StardardBase(AbstractDoubleProblem problem) {
        this.problem = problem;
    }
    
    protected double[] runTest(double[] inputValues){
        DoubleSolution s=new DefaultDoubleSolution(problem);
        for(int i=0; i< inputValues.length; i++){
            s.setVariableValue(i, inputValues[i]);
        }
        problem.evaluate(s);
        double[] f = new double[s.getNumberOfObjectives()];
        for(int i=0; i < s.getNumberOfObjectives(); i++){
            f[i]=s.getObjective(i);
        }
        return f;
    }
}
