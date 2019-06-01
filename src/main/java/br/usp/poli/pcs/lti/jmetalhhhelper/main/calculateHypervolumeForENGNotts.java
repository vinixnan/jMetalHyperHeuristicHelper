package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.Calculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.EpsilonCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.EpsilonPlusCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.HypervolumeCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.IgdCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.IgdPlusCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.WFGHypervolumeCalculator;
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
public class calculateHypervolumeForENGNotts {

    public static void main(String[] args) throws FileNotFoundException {

        boolean normalize=true;
        normalize=false;
        String ibeaPath = args[0];
        String spea2Path = args[1];
        String nsgaiiPath = args[2];
        String gde3Path = args[3];
        String mIBEAPath = args[4];
        String pfKnown = args[5];
        int m = Integer.parseInt(args[6]);
        int type = Integer.parseInt(args[7]);

        //System.out.println(Arrays.toString(args));
        //read files
        Front ibeaFront = new ArrayFront(ibeaPath);
        Front spea2Front = new ArrayFront(spea2Path);
        Front nsgaiiFront = new ArrayFront(nsgaiiPath);
        Front gde3Front = new ArrayFront(gde3Path);
        Front mIbeaFront = new ArrayFront(mIBEAPath);
        Front pfFront = new ArrayFront(pfKnown);
        //pfFront = calculateHypervolumeForENGNotts.removeNegative(pfFront, m);
        String dir = pfKnown + ".removedDominated.file";

        File f = new File(dir);
        if (f.exists()) {
            
            pfFront = new ArrayFront(dir);
        } else {
            System.err.println("Only you");
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
        System.err.println(Arrays.toString(nadir));
        ibeaFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(ibeaFront, nadir, m);
        spea2Front = calculateHypervolumeForENGNotts.removeWorseThanNadir(spea2Front, nadir, m);
        nsgaiiFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(nsgaiiFront, nadir, m);
        gde3Front = calculateHypervolumeForENGNotts.removeWorseThanNadir(gde3Front, nadir, m);
        mIbeaFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(mIbeaFront, nadir, m);


        Calculator fhc;
        switch (type) {
            case 0:
                fhc = new WFGHypervolumeCalculator(m, pfFront, normalize);
                break;
            case 1:
                fhc = new IgdCalculator(m, pfFront);
                break;
            case 2:
                 fhc = new EpsilonCalculator(m, pfFront, normalize);
                 break;
            case 3:
                 fhc = new IgdPlusCalculator(m, pfFront);
                 break;
            case 4:
                 fhc = new EpsilonPlusCalculator(m, pfFront);
                 break;
            default:
                fhc = new WFGHypervolumeCalculator(m, pfFront, normalize);
                break;
        }

        double ibeaHyp = 0;
        double spea2Hyp = 0;
        double nsgaiiHyp = 0;
        double gde3Hyp = 0;
        double mIbeaHyp = 0;

        ibeaHyp = calculateHypervolumeForENGNotts.calc(fhc, ibeaFront);
        spea2Hyp = calculateHypervolumeForENGNotts.calc(fhc, spea2Front);
        nsgaiiHyp = calculateHypervolumeForENGNotts.calc(fhc, nsgaiiFront);
        gde3Hyp = calculateHypervolumeForENGNotts.calc(fhc, gde3Front);
        mIbeaHyp = calculateHypervolumeForENGNotts.calc(fhc, mIbeaFront);

        DecimalFormat nf = new DecimalFormat("###.####################");
        String valueHH = "";
        if (args.length >= 9 && !args[8].isEmpty()) {
            String[] hhPaths =  Arrays.copyOfRange(args, 8, args.length);
            //System.out.println(Arrays.toString(hhPaths));
            Front[] hhFront = new Front[hhPaths.length];
            for (int i = 0; i < hhPaths.length; i++) {
                hhFront[i]=new ArrayFront(hhPaths[i]);
                hhFront[i] = calculateHypervolumeForENGNotts.removeWorseThanNadir(hhFront[i], nadir, m);
                double hhHyp = calculateHypervolumeForENGNotts.calc(fhc, hhFront[i]);
                valueHH += nf.format(hhHyp)+" ";
            }
            valueHH=valueHH.trim().replace(" ", ";");
        }

        System.out.println(nf.format(gde3Hyp) + ";" + nf.format(ibeaHyp) + ";" + nf.format(nsgaiiHyp) + ";" + nf.format(spea2Hyp) + ";" + nf.format(mIbeaHyp) + ";" + valueHH);
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
    
    public static ArrayFront removeNegative(Front front, int m) {
        List population = FrontUtils.convertFrontToSolutionList(front);
        List newpopulation = new ArrayList();
        for (Object o : population) {
            Solution s = (Solution) o;
            boolean stillOk = true;
            for (int i = 0; i < m && stillOk; i++) {
                if (s.getObjective(i) < 0) {
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
