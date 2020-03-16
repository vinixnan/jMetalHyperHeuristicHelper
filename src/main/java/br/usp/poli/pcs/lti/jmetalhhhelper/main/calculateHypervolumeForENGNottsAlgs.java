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
public class calculateHypervolumeForENGNottsAlgs {

    public static void main(String[] args) throws FileNotFoundException {

        String alg1Path = args[0];
        String alg2Path = args[1];
        String pfKnown = args[2];
        int m = Integer.parseInt(args[3]);
        int type = Integer.parseInt(args[4]);

        //System.out.println(Arrays.toString(args));
        //read files
        Front alg1Front = new ArrayFront(alg1Path);
        Front alg2Front = new ArrayFront(alg2Path);
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
        alg1Front = calculateHypervolumeForENGNottsAlgs.removeWorseThanNadir(alg1Front, nadir, m);
        alg2Front = calculateHypervolumeForENGNottsAlgs.removeWorseThanNadir(alg2Front, nadir, m);

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

        double alg1Hyp = 0;
        double alg2Hyp = 0;

        alg1Hyp = calculateHypervolumeForENGNottsAlgs.calc(fhc, alg1Front);
        alg2Hyp = calculateHypervolumeForENGNottsAlgs.calc(fhc, alg2Front);

        DecimalFormat nf = new DecimalFormat("###.####################");
        String valueHH = "";
        System.out.println(nf.format(alg1Hyp) + ";" + nf.format(alg2Hyp) + ";");
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
                if (nadir[i] < s.getObjective(i) || s.getObjective(i) < 0) {
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
