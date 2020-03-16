/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.poli.pcs.lti.jmetalhhhelper.util;

import br.usp.poli.pcs.lti.jmetalhhhelper.main.calculateHypervolumeForENGNotts;
import java.io.FileNotFoundException;

/**
 *
 * @author vinicius
 */
public class TestIT {
    public static void main(String[] args) throws FileNotFoundException {
        String[] par=new String[16];
        par[0]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/REALProblems/data/myIBEA/NeuralNetDoublePoleBalancing/FUN3.tsv"; 
        par[1]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/REALProblems/data/mySPEA2/NeuralNetDoublePoleBalancing/FUN3.tsv"; 
        par[2]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/REALProblems/data/myNSGAII/NeuralNetDoublePoleBalancing/FUN3.tsv";
        par[3]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/REALProblems/data/myGDE3/NeuralNetDoublePoleBalancing/FUN3.tsv"; 
        par[4]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/output/REALProblems/data/mIBEA/NeuralNetDoublePoleBalancing/FUN3.tsv"; 
        par[5]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/pfKnownNeuralNetDoublePoleBalancing_212_12_100"; 
        par[6]="2"; 
        par[7]="1";
        par[8]="NeuralNetDoublePoleBalancing";
        par[9]="/home/vinicius/store/vinicius/PJT/MOABHH-dev/result/MOABHH_100_12_12_Standard_Copeland/NeuralNetDoublePoleBalancing_2/FUN4.tsv";
        par[10]="/home/vinicius/store/vinicius/PJT/MOABHH-dev/result/MOABHH_100_12_12_Standard_Copeland/NeuralNetDoublePoleBalancing_2/FUN4.tsv";
        par[11]="/home/vinicius/store/vinicius/PJT/MOABHH-dev/result/MOABHH_100_12_12_Standard_Copeland/NeuralNetDoublePoleBalancing_2/FUN4.tsv";
        par[12]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/HF_Config_Benchmark/Results/Real/AM_10/LAResutls/run_3/LA_FinalParetoFront_Real8_Run3.txt"; 
        par[13]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/HF_Config_Benchmark/Results/Real/AM/DominanceInitalNew_10/run_3/LA_FinalParetoFront_Real8_Run3.txt"; 
        par[14]="/home/vinicius/store/vinicius/PJT/MOHH-LARILA/HF_Config_Benchmark/Results/Real/AM/CF/run_3/Real8CF_FinalParetoFront3.txt";
        par[15]="1";
        
        calculateHypervolumeForENGNotts.main(par);

    }
}
