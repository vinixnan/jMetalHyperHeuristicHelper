package br.usp.poli.pcs.lti.jmetalproblems.interfaces;

/**
 *
 * @author vinicius
 */
public interface RealWorldProblem {
    public boolean isConstrained();
    public boolean isDiscrete();
    public int getQtdEvaluated();
}
