package uk.ac.nottingham.asap.realproblems;

import br.usp.poli.pcs.lti.jmetalproblems.interfaces.RealWorldProblem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;


/**
 *
 * @author vinicius
 */
public abstract class ExternalProblem extends AbstractDoubleProblem implements RealWorldProblem {

    protected boolean windows;
    protected int qtdEvaluated;

    public ExternalProblem() {
        windows = (System.getProperty("os.name").equalsIgnoreCase("Windows"));
        qtdEvaluated = 0;
    }

    protected boolean isWindows() {
        return windows;
    }

    protected double[] callExternalEvaluation(String problemName, double[] x, int m) {
        double[] toReturn = new double[m];
        Arrays.fill(toReturn, 1.0);
        //Call wfgHypervolume
        String fileToExecute = "./realworld";
        if (isWindows()) {
            fileToExecute += ".exe";
        }
        ProcessBuilder pb = new ProcessBuilder();
        List<String> commands = new ArrayList<>();
        commands.add(fileToExecute);
        commands.add(problemName);
        for (int i = 0; i < x.length; i++) {
            commands.add(String.valueOf(x[i]));
        }
        pb.command(commands);
        pb.redirectErrorStream(true);
        Process process;
        try {
            process = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            //Take output
            ArrayList<String> lines = new ArrayList<>();
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if (!s.isEmpty()) {
                    lines.add(s);
                }
            }
            //take hypervolume result
            String result = lines.get(0).replace("{", "").replace("}", "").trim();
            String[] split = result.split(",");
            for (int i = 0; i < split.length; i++) {
                if (split[i].equals("-nan") || split[i].contains("Assert") || split[i].contains("inf") || split[i].equals("nan")) {
                    Arrays.fill(toReturn, 1.0);
                    i = split.length;
                } else {
                    toReturn[i] = Double.parseDouble(split[i]);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(VibratingPlatformDesign.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;

    }

    @Override
    public void evaluate(DoubleSolution solution) {
        double[] x = new double[solution.getNumberOfVariables()];
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            x[i] = solution.getVariableValue(i);
        }
        double[] fx = callExternalEvaluation(getName(), x, getNumberOfObjectives());
        solution.setObjective(0, fx[0]);
        solution.setObjective(1, fx[1]);
        qtdEvaluated++;
    }

    public int getQtdEvaluated() {
        return qtdEvaluated;
    }
}
