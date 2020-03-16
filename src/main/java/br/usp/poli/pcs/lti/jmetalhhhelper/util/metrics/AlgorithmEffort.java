package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics;

import lombok.Getter;
import lombok.Setter;
import org.uma.jmetal.util.front.Front;

public class AlgorithmEffort extends Calculator {

    protected @Getter @Setter long timeExpend;
    protected @Getter @Setter int evaluations;

    public AlgorithmEffort(int numObj) {
        super(numObj);
        this.timeExpend = 0;
        this.evaluations = 0;
        this.lowerValuesAreBetter=true;
        this.indicatorName = "AE";
    }

    @Override
    public double calculate(Front front, double[] doubles, double[] doubles1) {
        if (this.evaluations > 0 && timeExpend > 0) {
            return (((double) timeExpend) / ((double) this.evaluations));
        }
        return Double.MAX_VALUE/((double)Double.MAX_EXPONENT);
    }
    
    public double execute(long timeExpend, int evaluations) {
        this.timeExpend=timeExpend;
        this.evaluations=evaluations;
        return calculate(null, null, null);
    }
}
