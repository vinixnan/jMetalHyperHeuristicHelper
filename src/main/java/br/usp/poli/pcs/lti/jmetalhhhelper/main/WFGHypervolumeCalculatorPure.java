package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.ArrayFront;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics.WFGHypervolumeCalculator;
import com.google.common.primitives.Doubles;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;
import org.apache.commons.math3.stat.StatUtils;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author vinicius
 */
@Data
public class WFGHypervolumeCalculatorPure {

    private double nadirBase;
    private int m;
    private boolean normalize;

    public WFGHypervolumeCalculatorPure() {
        nadirBase = 1.0;
        m = 2;
        normalize = false;
    }

    public double calculate(String front) {
        try {
            return calculate(new ArrayFront(front));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WFGHypervolumeCalculatorPure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Double.NaN;
    }

    public double calculate(Front front) {
        double[] references = new double[m];
        Arrays.fill(references, nadirBase);
        return calculate(front, references);
    }
    
    public double calculate(Front front, double[] references) {
        List<PointSolution> population = FrontUtils.convertFrontToSolutionList(front);
        return calculate(population, references);
    }

    public double calculate(List front, double[] references) {

        try {
            
            List population=removeWorseThanNadir(front, references, m);
            population=SolutionListUtils.getNondominatedSolutions(population);
            //Save population to file
            long uId = System.currentTimeMillis();
            long snd = JMetalRandom.getInstance().nextInt(0, 100000);
            long snd2 = JMetalRandom.getInstance().nextInt(0, 100000);
            String prefix = String.valueOf(uId) + String.valueOf(snd) + String.valueOf(snd2);
            String path = System.getProperty("user.dir") + "/tempfront" + prefix;
            new SolutionListOutput(population)
                    .setSeparator(" ")
                    .setFunFileOutputContext(new DefaultFileOutputContext(path))
                    .print();

            //read file to format as wfg hypervolume uses
            Path filepath = (Path) Paths.get(System.getProperty("user.dir"), "tempfront" + prefix);
            List<String> lines = Files.readAllLines((java.nio.file.Path) filepath);
            List<String> newPrinting = new ArrayList<>();
            newPrinting.add("#");
            newPrinting.addAll(lines);
            newPrinting.add("#");

            //remove unecessary files
            File file = new File(path);
            file.delete();

            //Save formated file
            path += ".updated";
            try {
                PrintWriter writer = new PrintWriter(path, "UTF-8");
                for (String str : newPrinting) {
                    writer.println(str);
                }
                writer.close();
            } catch (IOException e) {
                // do something
            }

            //Call wfgHypervolume
            String fileToExecute = "./wfgfhypervolume";
            ProcessBuilder pb = new ProcessBuilder();
            List<String> commands = new ArrayList<>();
            commands.add(fileToExecute);
            commands.add(path);
            //add reference point
            for (int i = 0; i < m; i++) {
                commands.add(String.valueOf(references[i]));
            }
            pb.command(commands);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            //Take output
            lines = new ArrayList<>();
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                lines.add(s);
            }
            //take hypervolume result
            String result = lines.get(0).replace("hv(1) = ", "");

            //remove unecessary files
            file = new File(path);
            file.delete();

            return Double.parseDouble(result);
        } catch (IOException ex) {
            Logger.getLogger(WFGHypervolumeCalculator.class.getName()).log(Level.SEVERE, null, ex);
            return Double.NaN;
        }
    }
    
    public List removeWorseThanNadir(List population, double[] nadir, int m) {
        List newpopulation = new ArrayList();
        for (Object o : population) {
            Solution s = (Solution) o;
            boolean stillOk = true;
            for (int i = 0; i < m && stillOk; i++) {
                if (nadir[i] < s.getObjective(i)) {
                    //System.err.println(nadir[i]+" menor "+s.getObjective(i));
                    stillOk = false;
                }
                if(s.getObjective(i) < 0){
                    System.out.println("well");
                }
            }
            if (stillOk) {
                newpopulation.add(s);
            }
        }
        return newpopulation;
    }

    public static void main(String[] args) {
        int qtdExp=Integer.parseInt(args[1]);
        String basehhPath=args[2];
        String problem=args[0];
        double[] list=new double[qtdExp+1];
        WFGHypervolumeCalculatorPure calc = new WFGHypervolumeCalculatorPure();
        for (int n = 0; n <= qtdExp; n++) {
            String hhPath=basehhPath+n+".tsv";
            list[n]= calc.calculate(hhPath);
        }
        Arrays.sort(list);
        double mean=1-StatUtils.mean(list);
        double median=1-list[list.length/2];
        double worse=1-Doubles.min(list);
        double best=1-Doubles.max(list);
        System.out.println(problem+";"+best+";"+mean+";"+median+";"+worse);
    }
}
