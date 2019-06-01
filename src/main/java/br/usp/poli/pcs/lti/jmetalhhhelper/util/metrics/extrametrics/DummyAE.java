package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.extrametrics;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.AlgorithmEffort;
import lombok.Getter;
import lombok.Setter;
import org.uma.jmetal.util.front.Front;

/**
 *
 * @author vinicius
 */
public class DummyAE extends AlgorithmEffort {

    protected @Getter
    @Setter
    String algName;

    public DummyAE(int numberOfObjectives) {
        super(numberOfObjectives);
    }

    @Override
    public double calculate(Front front, double[] maximumValues, double[] minimumValues) {
        switch (algName.toLowerCase()) {
            case "ibea":
                return 4;
            case "mibea":
                return 3;
            case "nsgaii":
                return 2;
            case "spea2":
                return 5;
            case "gde3":
                return 1;
            default:
                return 1;
        }

    }

}
