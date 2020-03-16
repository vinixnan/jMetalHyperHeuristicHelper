package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.Calculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.EpsilonCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.HypervolumeCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.IgdCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.IgdPlusCalculator;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
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
public class calculateHypervolumeForENG {

    public static void main(String[] args) throws FileNotFoundException {

        String ibeaPath = args[0];
        String spea2Path = args[1];
        String nsgaiiPath = args[2];
        String gde3Path = args[3];
        String pfKnown = args[4];
        int m = Integer.parseInt(args[5]);
        int type = Integer.parseInt(args[6]);

        //System.out.println(Arrays.toString(args));
        //read files
        Front ibeaFront = new ArrayFront(ibeaPath);
        Front spea2Front = new ArrayFront(spea2Path);
        Front nsgaiiFront = new ArrayFront(nsgaiiPath);
        Front gde3Front = new ArrayFront(gde3Path);
        Front pfFront = new ArrayFront(pfKnown);

        String dir = pfKnown + ".removedDominated.file";

        File f = new File(dir);
        if (f.exists()) {
            pfFront = new ArrayFront(dir);
        } else {
            //generate ndominated front
            List<PointSolution> pflist = FrontUtils.convertFrontToSolutionList(pfFront);
            NonDominatedSolutionListArchive nd = new NonDominatedSolutionListArchive();
            for (PointSolution sol : pflist) {
                nd.add(sol);
            }
            pflist = nd.getSolutionList();
            pfFront = new ArrayFront(pflist);
            new SolutionListOutput(pflist)
                    .setSeparator("\t")
                    .setFunFileOutputContext(new DefaultFileOutputContext(dir))
                    .print();
        }

        //remove worser points
        double[] nadir = FrontUtils.getMaximumValues(pfFront);
        ibeaFront = calculateHypervolumeForENG.removeWorseThanNadir(ibeaFront, nadir, m);
        spea2Front = calculateHypervolumeForENG.removeWorseThanNadir(spea2Front, nadir, m);
        nsgaiiFront = calculateHypervolumeForENG.removeWorseThanNadir(nsgaiiFront, nadir, m);
        gde3Front = calculateHypervolumeForENG.removeWorseThanNadir(gde3Front, nadir, m);

        Calculator fhc;
        switch (type) {
            case 0:
                fhc = new HypervolumeCalculator(m, pfFront);
                break;
            case 1:
                fhc = new IgdCalculator(m, pfFront);
                break;
            case 2:
                 fhc = new EpsilonCalculator(m, pfFront);
                 break;
            case 3:
                 fhc = new IgdPlusCalculator(m, pfFront);
                 break;
            default:
                fhc = new HypervolumeCalculator(m, pfFront);
                break;
        }

        double ibeaHyp = 0;
        double spea2Hyp = 0;
        double nsgaiiHyp = 0;
        double gde3Hyp = 0;

        ibeaHyp = calculateHypervolumeForENG.calc(fhc, ibeaFront);
        spea2Hyp = calculateHypervolumeForENG.calc(fhc, spea2Front);
        nsgaiiHyp = calculateHypervolumeForENG.calc(fhc, nsgaiiFront);
        gde3Hyp = calculateHypervolumeForENG.calc(fhc, gde3Front);

        DecimalFormat nf = new DecimalFormat("###.####################");
        String valueHH = "";
        if (args.length >= 8 && !args[7].isEmpty()) {
            String[] hhPaths =  Arrays.copyOfRange(args, 7, args.length);
            //System.out.println(Arrays.toString(hhPaths));
            Front[] hhFront = new Front[hhPaths.length];
            for (int i = 0; i < hhPaths.length; i++) {
                hhFront[i]=new ArrayFront(hhPaths[i]);
                hhFront[i] = calculateHypervolumeForENG.removeWorseThanNadir(hhFront[i], nadir, m);
                double hhHyp = calculateHypervolumeForENG.calc(fhc, hhFront[i]);
                valueHH += nf.format(hhHyp)+" ";
            }
            valueHH=valueHH.trim().replace(" ", ";");
        }

        System.out.println(nf.format(gde3Hyp) + ";" + nf.format(ibeaHyp) + ";" + nf.format(nsgaiiHyp) + ";" + nf.format(spea2Hyp) + ";" + valueHH);
    }

    public static double calc(Calculator fhc, Front front) {
        if (front == null) {
            return 0D;
        }
        return fhc.execute(front);
    }

    public static ArrayFront removeWorseThanNadir(Front front, double[] nadir, int m) {
        List population = FrontUtils.convertFrontToSolutionList(front);
        List newpopulation = new ArrayList();
        for (Object o : population) {
            Solution s = (Solution) o;
            boolean stillOk = true;
            for (int i = 0; i < m && stillOk; i++) {
                if (nadir[i] < s.getObjective(i)) {
                    //System.err.println(nadir[i]+" menor "+s.getObjective(i));
                    stillOk = false;
                }
            }
            if (stillOk) {
                newpopulation.add(s);
            }
        }
        if (!newpopulation.isEmpty()) {
            return new ArrayFront(newpopulation);
        }
        return null;
    }
}
