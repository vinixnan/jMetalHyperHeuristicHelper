package br.usp.poli.pcs.lti.jmetalhhhelper.util.metrics;

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
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 *
 * @author vinicius
 */
public class WFGHypervolumeCalculator extends HypervolumeCalculator {

    public WFGHypervolumeCalculator(int numberOfObjectives) {
        super(numberOfObjectives);
    }

    public WFGHypervolumeCalculator(int numberOfObjectives, Front referenceFront, boolean normalize) throws FileNotFoundException {
        super(numberOfObjectives, referenceFront, normalize);
    }

    public WFGHypervolumeCalculator(int numberOfObjectives, Front referenceFront) throws FileNotFoundException {
        super(numberOfObjectives, referenceFront, true);
    }

    @Override
    public double calculate(Front front, double[] maximumValues, double[] minimumValues) {
        if (front != null && maximumValues != null && minimumValues != null) {
            double[] references = new double[numberOfObjectives];
            if (normalize) {
                if (!this.isMinMaxTheSame(maximumValues, minimumValues)) {
                    FrontNormalizer frontNormalizer = new FrontNormalizer(minimumValues, maximumValues);
                    front = frontNormalizer.normalize(front);
                }
                Arrays.fill(references, 1.01);
            } else {
                references = maximumValues;
            }
            try {
                List<PointSolution> population = FrontUtils.convertFrontToSolutionList(front);
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
                for (int i = 0; i < this.numberOfObjectives; i++) {
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
            }

        }
        return 0D;
    }
}