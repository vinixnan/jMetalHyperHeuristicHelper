package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.ArrayFront;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.front.Front;

import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

/**
 * This class extends Calculator class and perform an evaluation using
 * Hypervolume Indicator.
 */
public class HypervolumeCalculator extends Calculator {

    /**
     * Instantiates a new Hypervolume calculator.
     *
     * @param numberOfObjectives the number of objectives
     */
    public HypervolumeCalculator(int numberOfObjectives) {
        super(numberOfObjectives);
        this.indicatorName = "Hypervolume";
        this.lowerValuesAreBetter = new PISAHypervolume().isTheLowerTheIndicatorValueTheBetter();
    }

    /**
     * Instantiates a new Hypervolume calculator.
     *
     * @param numberOfObjectives the number of objectives
     * @param path the path
     * @throws FileNotFoundException the file not found exception
     */
    public HypervolumeCalculator(int numberOfObjectives, String path) throws FileNotFoundException {
        this(numberOfObjectives, new ArrayFront(path), true);
        this.lowerValuesAreBetter = new PISAHypervolume().isTheLowerTheIndicatorValueTheBetter();
    }

    /**
     * Instantiates a new Hypervolume calculator.
     *
     * @param numberOfObjectives the number of objectives
     * @param referenceFront the reference front
     * @param normalize
     * @throws FileNotFoundException the file not found exception
     */
    public HypervolumeCalculator(int numberOfObjectives, Front referenceFront, boolean normalize)
            throws FileNotFoundException {
        super(numberOfObjectives, referenceFront, normalize);
        this.indicatorName = "Hypervolume";
        this.lowerValuesAreBetter = new PISAHypervolume().isTheLowerTheIndicatorValueTheBetter();
    }
    
    public HypervolumeCalculator(int numberOfObjectives, Front referenceFront)
            throws FileNotFoundException {
        super(numberOfObjectives, referenceFront, true);
        this.indicatorName = "Hypervolume";
        this.lowerValuesAreBetter = new PISAHypervolume().isTheLowerTheIndicatorValueTheBetter();
    }

    public void updatePointsUsingNadir(double[] nadir) {
        maximumValues = nadir;
        minimumValues = new double[numberOfObjectives];
        Arrays.fill(minimumValues, 0);
    }

    @Override
    public double calculate(Front front, double[] maximumValues, double[] minimumValues) {
        if (normalize) {
            if (maximumValues != null && minimumValues != null) {
                if (!this.isMinMaxTheSame(maximumValues, minimumValues)) {
                    FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues);
                    front = frontNormalizer.normalize(front);
                }
            } else {
                return 0D;
            }
        }
        List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(front);
        return new PISAHypervolume<PointSolution>(paretoTrueFront).evaluate(normalizedPopulation);

    }
}
