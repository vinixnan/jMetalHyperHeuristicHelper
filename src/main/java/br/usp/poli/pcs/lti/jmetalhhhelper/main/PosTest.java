package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.WFGHypervolumeCalculator;

import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.Calculator;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.IgdCalculator;
import br.usp.poli.pcs.lti.jmetalproblems.util.ProblemFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

/**
 *
 * @author vinicius
 */
public class PosTest {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String problemName = args[0];
        int m = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);
        int l = Integer.parseInt(args[3]);
        String gde3Path = args[4];
        String moeaddPath = args[5];
        String mombiPath = args[6];
        String nsgaiiPath = args[7];
        String maebPath = args[8];
        int metric = Integer.parseInt(args[9]);
        int popSize = Integer.parseInt(args[10]);

        ArrayFront gdeFront = new ArrayFront(gde3Path);
        ArrayFront moeaddFront = new ArrayFront(moeaddPath);
        ArrayFront mombiFront = new ArrayFront(mombiPath);
        ArrayFront nsgaiiFront = new ArrayFront(nsgaiiPath);
        ArrayFront maebFront = new ArrayFront(maebPath);

        double[] nadir = PosTest.getNadir(problemName, m);

        //System.err.println(problemName+" "+m+" "+ ""+Arrays.toString(nadir));
        double hypGde3 = 0, hypMoead = 0, hypMombi = 0, hypNsgaii = 0, hypMaeb = 0;

        Calculator fhc;
        if (metric == 0) {
            gdeFront = PosTest.removeWorseThanNadir(gdeFront, nadir, m);
            moeaddFront = PosTest.removeWorseThanNadir(moeaddFront, nadir, m);
            mombiFront = PosTest.removeWorseThanNadir(mombiFront, nadir, m);
            nsgaiiFront = PosTest.removeWorseThanNadir(nsgaiiFront, nadir, m);
            maebFront = PosTest.removeWorseThanNadir(maebFront, nadir, m);
            fhc = new WFGHypervolumeCalculator(m);
            ((WFGHypervolumeCalculator) fhc).updatePointsUsingNadir(nadir);
            hypGde3 = fhc.execute(gdeFront);
            hypMoead = fhc.execute(moeaddFront);
            hypMombi = fhc.execute(mombiFront);
            hypNsgaii = fhc.execute(nsgaiiFront);
            hypMaeb = fhc.execute(maebFront);
        } else {
            Problem problem = ProblemFactory.getProblem(problemName, k, l, m);
            String pf
                    = "pareto_fronts/" + problem.getName() + "." + problem.getNumberOfObjectives() + "D.pf";

            ArrayFront paretoFront = new ArrayFront(pf);
            fhc = new IgdCalculator(m, paretoFront);
            hypGde3 = fhc.execute(gdeFront);
            hypMoead = fhc.execute(moeaddFront);
            hypMombi = fhc.execute(mombiFront);
            hypNsgaii = fhc.execute(nsgaiiFront);
            hypMaeb = fhc.execute(maebFront);
        }

        DecimalFormat nf = new DecimalFormat("###.####################");

        System.out.println(nf.format(hypGde3) + ";" + nf.format(hypMoead) + ";" + nf.format(hypMombi) + ";" + nf.format(hypNsgaii) + ";" + nf.format(hypMaeb));
    }
    
     public static ArrayFront removeWorseThanNadir(Front front, double[] nadir, int m) {
        List population = FrontUtils.convertFrontToSolutionList(front);
        List newpopulation = PosTest.removeWorseThanNadir(population, nadir, m);
        if (!newpopulation.isEmpty()) {
            return new ArrayFront(newpopulation);
        }
        return null;
    }

    public static List removeWorseThanNadir(List population, double[] nadir, int m) {
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
        return newpopulation;
    }

    public static double[] getNadir(String problemName, int m) {
        double[] nadir = new double[m];
        if (problemName.contains("WFG")) {
            double[] base = new double[]{3.0, 5.0, 7.0, 9.0, 11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0, 25.0, 27.0, 29.0, 31.0, 33.0, 35.0, 37.0, 39.0, 41.0, 43.0};
            nadir = Arrays.copyOf(base, m);
        } else if (problemName.contains("DTLZ1") && m==3) {
            Arrays.fill(nadir, 1.0);
        }
        else if (problemName.contains("DTLZ1")) {
            Arrays.fill(nadir, 10.0);
        }
        else if (problemName.contains("DTLZ")  && m==3) {
            Arrays.fill(nadir, 2.0);
        }
        else if (problemName.contains("DTLZ")) {
            Arrays.fill(nadir, 20.0);
        }
        return nadir;
    }
}
