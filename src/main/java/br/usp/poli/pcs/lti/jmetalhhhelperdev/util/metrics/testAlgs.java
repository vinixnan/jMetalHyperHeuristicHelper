/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.poli.pcs.lti.jmetalhhhelperdev.util.metrics;

import br.usp.poli.pcs.lti.jmetalhhhelper.core.ParametersforAlgorithm;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.ParametersforHeuristics;
import br.usp.poli.pcs.lti.jmetalhhhelper.core.interfaces.LLHInterface;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.AlgorithmBuilder;
import br.usp.poli.pcs.lti.jmetalhhhelper.util.ProblemFactoryExtra;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import javax.management.JMException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.uma.jmetal.problem.Problem;

/**
 *
 * @author vinicius
 */
public class testAlgs {

    public static void eval(String problemName, int popSize, String algConfigFileName, String parameterHeuristic) throws ConfigurationException, JMException, FileNotFoundException {
        int numGenerations = 0;
        if (problemName.equals("DiskBrakeDesign") || problemName.equals("WeldedBeamDesign") || problemName.equals("KernelRidgeRegressionParameterTuning")) {
            numGenerations = 100;
        } else if (problemName.equals("VibratingPlatformDesign") || problemName.equals("OpticalFilter")) {
            numGenerations = 200;
        } else if (problemName.equals("HeatExchanger") || problemName.equals("AucMaximization")) {
            numGenerations = 500;
        } else if (problemName.equals("HydroDynamics") || problemName.equals("FacilityPlacement")) {
            numGenerations = 1000;
        } else if (problemName.equals("NeuralNetDoublePoleBalancing")) {
            numGenerations = 2000;
        }
        numGenerations=numGenerations/popSize;
        numGenerations=1; popSize=100;
        Problem[] problems = ProblemFactoryExtra.getProblems(problemName, 4, 20, 2);
        Problem problem = problems[0];
        ParametersforAlgorithm paramAlg = new ParametersforAlgorithm(algConfigFileName);
        paramAlg.setMaxIteractions(numGenerations);
        paramAlg.setPopulationSize(popSize);
        paramAlg.setArchiveSize(popSize);
        ParametersforHeuristics pHeur = new ParametersforHeuristics(parameterHeuristic, problem.getNumberOfVariables());
        AlgorithmBuilder hb = new AlgorithmBuilder(problem);
        LLHInterface alg = hb.create(paramAlg, pHeur);
        long begin=System.currentTimeMillis();
        alg.run();
        List result=alg.getResult();
        long end=System.currentTimeMillis();
        WFGHypervolumeCalculatorPure calc = new WFGHypervolumeCalculatorPure();
        double[] ref=new double[2];
        Arrays.fill(ref, 1.0);
        double val=calc.calculate(result, ref);
        double minutes=(((double)(end-begin))/1000.0)/60.0;
        System.out.println(problemName+";"+alg.getClass().getSimpleName()+";"+(1-val)+" in "+minutes+" minutes");
        //0.24979152839999996 0.24949246430000005
    }
    
    public static void main(String[] args) throws ConfigurationException, JMException, FileNotFoundException {
        String[] confs={"GDE3.default", "IBEA.default", "NSGAII.default", "SPEA2.default", "mIBEA.default"};
        //String[] confs={"GDE3.default"};
        //String[] problems={"DiskBrakeDesign", "WeldedBeamDesign", "VibratingPlatformDesign", "OpticalFilter", "HeatExchanger", "AucMaximization", "HydroDynamics", "FacilityPlacement", "NeuralNetDoublePoleBalancing", "KernelRidgeRegressionParameterTuning"};
        String[] problems={"NeuralNetDoublePoleBalancing", "KernelRidgeRegressionParameterTuning"};
        int popSize=24;
        int qtdExp=1;
        for(String problemName : problems){
            for(String cf : confs){
                String parameterHeuristic = "SBX.Poly.default";
                if(cf.equals("GDE3.default")){
                    parameterHeuristic="DE.Poly.default";
                }
                for (int i = 0; i < qtdExp; i++) {
                    eval(problemName, popSize, cf, parameterHeuristic);
                }
            }
        }
        
        
        
        
        
    }
}
