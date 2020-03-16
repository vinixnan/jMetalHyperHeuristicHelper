/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import java.io.FileNotFoundException;

/**
 *
 * @author vinicius
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException{
        String[] ap={"/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/PreviousREALProblems/data/myIBEA/Machining/FUN0.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/PreviousREALProblems/data/mySPEA2/Machining/FUN0.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/PreviousREALProblems/data/myNSGAII/Machining/FUN0.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/PreviousREALProblems/data/myGDE3/Machining/FUN0.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/PreviousREALProblems/data/mIBEA/Machining/FUN0.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/pfKnownMachining_450_50_100", 
            "4", "0", "Machining", 
            "/home/vinicius/store/vinicius/PJT/MOABHH-dev/result/MOABHH_100_50_50_Standard_Copeland/Machining_4/FUN1.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOABHH-dev/result/MOABHH_100_50_50_Standard_Copeland/Machining_4/FUN1.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOABHH-dev/result/MOABHH_100_50_50_Standard_Copeland/Machining_4/FUN1.tsv", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/HF_Config_Benchmark/Results/VC/AM_10/LAResutls/run_1/LA_FinalParetoFront_Real7_Run1.txt", 
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/HF_Config_Benchmark/Results/VC/AM/DominanceInitalNew_10/run_1/LA_FinalParetoFront_Real7_Run1.txt",
            "/home/vinicius/store/vinicius/PJT/MOHH-LARILA/HF_Config_Benchmark/Results/VC/AM/CF/run_1/VC7CF_FinalParetoFront1.txt", 
            "1"};
        calculateHypervolumeForENGNotts.main(ap);

    }
}
