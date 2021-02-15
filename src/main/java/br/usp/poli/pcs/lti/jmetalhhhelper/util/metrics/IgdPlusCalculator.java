package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

/**
 * This class extends Calculator class and perform an evaluation using IGD
 * Indicator.
 */
public class IgdPlusCalculator extends Calculator {

    /**
     * IGD indicator instance.
     */
    private final InvertedGenerationalDistancePlus igd;

    /**
     * Instantiates a new Igd calculator.
     *
     * @param numberOfObjectives the number of objectives
     * @param path the path
     * @throws java.io.FileNotFoundException file not found.
     */
    public IgdPlusCalculator(int numberOfObjectives, String path) throws FileNotFoundException {
        super(numberOfObjectives, path);
        this.igd = new InvertedGenerationalDistancePlus(paretoTrueFront);
        this.indicatorName = "IGDPlus";
        this.lowerValuesAreBetter = igd.isTheLowerTheIndicatorValueTheBetter();
    }

    public IgdPlusCalculator(int numberOfObjectives, Front referenceFront, boolean normalize) throws FileNotFoundException {
        this(numberOfObjectives, referenceFront);
        this.normalize = normalize;
    }

    /**
     * Instantiates a new Igd calculator.
     *
     * @param numberOfObjectives the number of objectives
     * @param paretoFront the pareto front
     */
    public IgdPlusCalculator(int numberOfObjectives, Front paretoFront) {
        super(numberOfObjectives, paretoFront, true);
        this.igd = new InvertedGenerationalDistancePlus(paretoTrueFront);
        this.indicatorName = "IGDPlus";
        this.lowerValuesAreBetter = igd.isTheLowerTheIndicatorValueTheBetter();
    }

    @Override
    public double calculate(Front front, double[] maximumValues, double[] minimumValues) {
        double val = 1;
        if (maximumValues != null && minimumValues != null) {
            if (normalize) {
                if (!this.isMinMaxTheSame(maximumValues, minimumValues)) {
                    FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues);
                    front = frontNormalizer.normalize(front);
                }
            }
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(front);
            val = igd.evaluate(normalizedPopulation);
        }
        return val;
    }
}
