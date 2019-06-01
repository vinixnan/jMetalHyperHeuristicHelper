package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

/**
 *
 * @author vinicius
 */
public class generatePFKnown {

    public static void main(String[] args) throws FileNotFoundException {
        String input = args[0];
        String pfpath = args[1];
        int m = Integer.parseInt(args[2]);
        String output = args[3];

        Front inputFront = new ArrayFront(input);
        Front pfFront = new ArrayFront(pfpath);
        pfFront = calculateHypervolumeForENGNotts.removeNegative(pfFront, m);
        //generate ndominated front
        List<PointSolution> pflist = FrontUtils.convertFrontToSolutionList(pfFront);
        NonDominatedSolutionListArchive nd = new NonDominatedSolutionListArchive();
        for (PointSolution sol : pflist) {
            nd.add(sol);
        }
        pflist = nd.getSolutionList();
        pfFront = new ArrayFront(pflist);

        double[] nadir = FrontUtils.getMaximumValues(pfFront);

        inputFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(inputFront, nadir, m);

        pflist = FrontUtils.convertFrontToSolutionList(inputFront);
        nd = new NonDominatedSolutionListArchive();
        for (PointSolution sol : pflist) {
            nd.add(sol);
        }
        pflist = nd.getSolutionList();
        new SolutionListOutput(pflist)
                .setSeparator("\t")
                .setFunFileOutputContext(new DefaultFileOutputContext(output))
                .print();

    }
}
