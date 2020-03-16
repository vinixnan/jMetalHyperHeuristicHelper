package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.Calculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.EpsilonCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.EpsilonPlusCalculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.ArrayFront;
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
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

/**
 *
 * @author vinicius
 */
public class calculateHypervolumeForENGNotts {

    static boolean discardNegative=false;
    
    public static void main(String[] args) throws FileNotFoundException {

        int normalizeToken=Integer.parseInt(args[args.length-1]);
        boolean normalize=normalizeToken==1;
        String ibeaPath = args[0];
        String spea2Path = args[1];
        String nsgaiiPath = args[2];
        String gde3Path = args[3];
        String mIBEAPath = args[4];
        String pfKnown = args[5];
        int m = Integer.parseInt(args[6]);
        int type = Integer.parseInt(args[7]);
        String problem = args[8];

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
        boolean isBB=false;
        if(problem.equals("NeuralNetDoublePoleBalancing") || problem.equals("AucMaximization") || problem.equals("KernelRidgeRegressionParameterTuning") || problem.equals("FacilityPlacement") || problem.equals("HeatExchanger") || problem.equals("DiskBrakeDesign") || problem.equals("WeldedBeamDesign") || problem.equals("HydroDynamics") || problem.equals("OpticalFilter") || problem.equals("VibratingPlatformDesign")){
            isBB=true;
        }

        File f = new File(dir);
        if (f.exists()) {
            
            pfFront = new ArrayFront(dir);
        } else {
            //generate ndominated front
            
            if(calculateHypervolumeForENGNotts.discardNegative){
                pfFront = calculateHypervolumeForENGNotts.removeNegative(pfFront, m);
                if(pfFront==null){
                    calculateHypervolumeForENGNotts.discardNegative=false;
                    pfFront = new ArrayFront(pfKnown);
                    System.err.println("NULL FRONT");
                }
                else{
                    System.err.println("remove negatives before everything");
                }
            }
            
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
        
        m=nadir.length;
        //System.err.println(problem+" before "+Arrays.toString(nadir));
        boolean situation=isBB && !normalize;
        if(situation){
            Arrays.fill(nadir, 1.0);
        }
        
        
        pfFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(pfFront, nadir, m);//remove non 0
        ibeaFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(ibeaFront, nadir, m);
        spea2Front = calculateHypervolumeForENGNotts.removeWorseThanNadir(spea2Front, nadir, m);
        nsgaiiFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(nsgaiiFront, nadir, m);
        gde3Front = calculateHypervolumeForENGNotts.removeWorseThanNadir(gde3Front, nadir, m);
        mIbeaFront = calculateHypervolumeForENGNotts.removeWorseThanNadir(mIbeaFront, nadir, m);
        
        
        if(calculateHypervolumeForENGNotts.discardNegative){
            ibeaFront = calculateHypervolumeForENGNotts.removeNegative(ibeaFront, m);
            spea2Front = calculateHypervolumeForENGNotts.removeNegative(spea2Front, m);
            nsgaiiFront = calculateHypervolumeForENGNotts.removeNegative(nsgaiiFront, m);
            gde3Front = calculateHypervolumeForENGNotts.removeNegative(gde3Front, m);
            mIbeaFront = calculateHypervolumeForENGNotts.removeNegative(mIbeaFront, m);
        }
        
        double[] ideal = FrontUtils.getMinimumValues(pfFront);
        //ideal = new double[m];
        
        if(!situation){
            for (int i = 0; i < nadir.length; i++) {
                nadir[i]=nadir[i]*1.01;
            }
        }
        //if(problem.equals("NeuralNetDoublePoleBalancing") || problem.equals("AucMaximization") || problem.equals("KernelRidgeRegressionParameterTuning") || problem.equals("FacilityPlacement") || problem.equals("HeatExchanger") || problem.equals("DiskBrakeDesign") || problem.equals("WeldedBeamDesign") || problem.equals("HydroDynamics") || problem.equals("OpticalFilter") || problem.equals("VibratingPlatformDesign")){
        //    Arrays.fill(nadir, 1.0);
        //}
        System.err.println(problem+" "+Arrays.toString(nadir)+ " and ideal "+Arrays.toString(ideal));
        
        
        /*
        System.err.print("Ibea:"+ibeaFront.getNumberOfPoints()+" ");
        System.err.print("spea2:"+spea2Front.getNumberOfPoints()+" ");
        System.err.print("nsgaii:"+nsgaiiFront.getNumberOfPoints()+" ");
        System.err.print("gde3:"+gde3Front.getNumberOfPoints()+" ");
        System.err.print("mIbea:"+mIbeaFront.getNumberOfPoints()+" ");
*/
        WFGHypervolumeCalculatorPure wp=new WFGHypervolumeCalculatorPure();
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
                 fhc = new EpsilonPlusCalculator(m, pfFront, normalize);
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
        
        if(type!=6){
            ibeaHyp = calculateHypervolumeForENGNotts.calc(fhc, ibeaFront, nadir, ideal);
            spea2Hyp = calculateHypervolumeForENGNotts.calc(fhc, spea2Front, nadir, ideal);
            nsgaiiHyp = calculateHypervolumeForENGNotts.calc(fhc, nsgaiiFront, nadir, ideal);
            gde3Hyp = calculateHypervolumeForENGNotts.calc(fhc, gde3Front, nadir, ideal);
            mIbeaHyp = calculateHypervolumeForENGNotts.calc(fhc, mIbeaFront, nadir, ideal);
        }
        else{
            //System.err.println("PRINTA before");
            ibeaHyp = calculateHypervolumeForENGNotts.calc(wp, ibeaFront, nadir);
            spea2Hyp = calculateHypervolumeForENGNotts.calc(wp, spea2Front, nadir);
            nsgaiiHyp = calculateHypervolumeForENGNotts.calc(wp, nsgaiiFront, nadir);
            gde3Hyp = calculateHypervolumeForENGNotts.calc(wp, gde3Front, nadir);
            mIbeaHyp = calculateHypervolumeForENGNotts.calc(wp, mIbeaFront, nadir);
            //System.err.println("PRINTA after");
        }

        DecimalFormat nf = new DecimalFormat("###.####################");
        String valueHH = "";
        if (args.length >= 10 && !args[9].isEmpty()) {
            String[] hhPaths =  Arrays.copyOfRange(args, 9, args.length-1);
            //System.out.println(Arrays.toString(hhPaths));
            Front[] hhFront = new Front[hhPaths.length];
            for (int i = 0; i < hhPaths.length; i++) {
                hhFront[i]=new ArrayFront(hhPaths[i]);
                //System.err.println("PRINTA "+hhPaths[i]);
                
                if(calculateHypervolumeForENGNotts.discardNegative){
                    hhFront[i] = calculateHypervolumeForENGNotts.removeNegative(hhFront[i], m);
                }
                hhFront[i] = calculateHypervolumeForENGNotts.removeWorseThanNadir(hhFront[i], nadir, m);
                //System.err.print("HHX:"+hhFront[i].getNumberOfPoints()+" ");
                double hhHyp = 0;
                if(type!=6){
                    hhHyp = calculateHypervolumeForENGNotts.calc(fhc, hhFront[i], nadir, ideal);
                }
                else{
                    hhHyp = calculateHypervolumeForENGNotts.calc(wp, hhFront[i], nadir);
                }
                valueHH += nf.format(hhHyp)+" ";
            }
            valueHH=valueHH.trim().replace(" ", ";");
        }

        System.out.println(nf.format(gde3Hyp) + ";" + nf.format(ibeaHyp) + ";" + nf.format(nsgaiiHyp) + ";" + nf.format(spea2Hyp) + ";" + nf.format(mIbeaHyp) + ";" + valueHH);
    }

    public static double calc(Calculator fhc, Front front, double[] nadir, double[] ideal) {
        if (front == null) {
            return 0D;
        }
        return fhc.execute(front, nadir, ideal);
    }
    
    public static double calc(WFGHypervolumeCalculatorPure fhc, Front front, double[] references) {
        if (front == null) {
            return 0D;
        }
        fhc.setM(references.length);
        return fhc.calculate(front, references);
    }

    public static ArrayFront removeWorseThanNadir(Front front, double[] nadir, int m) {
        if(front!=null){
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
        }
        return null;
    }
    
    public static ArrayFront removeNegative(Front front, int m) {
        if(front!=null){
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
        }
        return null;
    }
}
