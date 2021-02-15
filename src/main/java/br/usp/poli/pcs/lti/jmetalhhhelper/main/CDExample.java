/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.poli.pcs.lti.jmetalhhhelper.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.algorithm.multiobjective.spea2.util.EnvironmentalSelection;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.wfg.WFG1;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

/**
 *
 * @author vinicius
 */
public class CDExample {
    
    public static Map<String,List<Integer>> selected;
    
    public static DoubleSolution generateSolution(double x, double y, int id){
        DoubleProblem p=new WFG1();
        DoubleSolution s=new DefaultDoubleSolution(p);
        s.setObjective(0, x);
        s.setObjective(1, y);
        s.setAttribute("id", id);
        return s;
    }
    
    public static void print(List<DoubleSolution> resp){
        for(DoubleSolution s : resp){
            Map mp=s.getAttributes();
            for(Object ob : mp.keySet()){
                System.out.print(String.valueOf(ob).replace("class org.uma.jmetal.util.solutionattribute.impl.", "")+" "+String.valueOf(mp.get(ob))+" ");
            }
            System.out.println(Arrays.toString(s.getObjectives()));
        }
    }
    
     
    
    public static void printX(List<DoubleSolution> resp){
        System.out.println("id,x,y,Fitness,DominanceRanking, Crowding, StrengthRawFitness");
        for(DoubleSolution s : resp){
            System.out.print(s.getAttribute("id")+",");
            System.out.print(Arrays.toString(s.getObjectives()).replace("[", "").replace("]", ""));
            Map mp=s.getAttributes();
            for(Object ob : mp.keySet()){
                if(String.valueOf(ob)!="id"){
                    System.out.print(","+String.valueOf(mp.get(ob)));
                }
            }
            System.out.println("");
        }
    }
    
    public static List<DoubleSolution> crowding(List<DoubleSolution> p, int max){
        List<DoubleSolution> pop=new ArrayList<>(p);
        RankingAndCrowdingSelection<DoubleSolution> rankingAndCrowdingSelection ;
        rankingAndCrowdingSelection = new RankingAndCrowdingSelection<DoubleSolution>(max, new DominanceComparator<>()) ;
        List<DoubleSolution> resp=rankingAndCrowdingSelection.execute(pop);
        System.out.println("NSGA-II before");
        print(pop);
        System.out.println("NSGA-II after");
        addResult(resp, "NSGA-II");
        print(resp);
        return resp;
    }
    
    public static void ibea(List<DoubleSolution> p, int max, String alg){
        System.out.println("-------------------------IBEA BEFORE");
        List<DoubleSolution> pop=new ArrayList<>(p);
        IBEA ibx=new IBEA(new WFG1(), max, max, 0, null, null,null);
        List<DoubleSolution> auxlist=new ArrayList<>(pop);
        ibx.calculateFitness(auxlist);
        print(auxlist);
        System.out.println("-------------------------IBEA AFTER");
        while (auxlist.size() > max) {
            ibx.removeWorst(auxlist);
        }
        print(auxlist);
        addResult(auxlist, alg);
    } 
    
    public static void spea2(List<DoubleSolution> p, int max){
        List<DoubleSolution> pop=new ArrayList<>(p);
        StrengthRawFitness<DoubleSolution> strenghtRawFitness = new StrengthRawFitness<DoubleSolution>(2);
        EnvironmentalSelection<DoubleSolution> myenvironmentalSelection = new EnvironmentalSelection<DoubleSolution>(max, 2);
        strenghtRawFitness.computeDensityEstimator(pop);
        System.out.println("-------------------------SPEA2 BEFORE");
        print(pop);
        List<DoubleSolution> archive = myenvironmentalSelection.execute(pop);
        System.out.println("-------------------------SPEA2 AFTER");
        print(archive);
        addResult(archive, "SPEA2");
    }
    
    public static void addResult(List<DoubleSolution> resp, String alg){
        List<Integer> l=new ArrayList<>();
        for(DoubleSolution s : resp){
            l.add((Integer) s.getAttribute("id"));
        }
        CDExample.selected.put(alg, l);
    }
    
    public static void main(String[] args){
        List<DoubleSolution> pop=new ArrayList<>();
        CDExample.selected=new HashMap<>();
        pop.add(generateSolution(1.5, 7.0,1));
        pop.add(generateSolution(2.5, 5,2));
        pop.add(generateSolution(3.0, 3.0,3));
        pop.add(generateSolution(3.5, 2,4));
        pop.add(generateSolution(2.5, 11.0,5));
        pop.add(generateSolution(3.5, 9.5,6));
        pop.add(generateSolution(4, 9,7));
        pop.add(generateSolution(4.7, 6.8,8));
        pop.add(generateSolution(5.5, 4.5,9));
        pop.add(generateSolution(6, 3.5,10));
        pop.add(generateSolution(4.7, 13.0,11));
        pop.add(generateSolution(5.5, 11.0,12));
        pop.add(generateSolution(6.5, 8.5,13));
        pop.add(generateSolution(6.8, 7.5,14));
        
        /*
        pop.add(generateSolution(2, 6,15));
        pop.add(generateSolution(2.6, 4.9,16));
        pop.add(generateSolution(2.9, 3.1,17));
        pop.add(generateSolution(3.6, 2,1));
        pop.add(generateSolution(4, 1,1));
*/
        
        for(DoubleSolution s : pop){
            System.out.println(Arrays.toString(s.getObjectives()).replace("[","").replace("]",""));
        }
        
        int max=7;
        
        print(crowding(pop, max));
        ibea(pop, max, "IBEA");
        System.out.println("mIBEA--------------");
        ibea(crowding(pop, max), max, "mIBEA");
        spea2(pop, max);
        
        printX(pop);
        
        for(String alg : selected.keySet()){
            System.out.print(alg+";");
            for(Integer i : selected.get(alg)){
                System.out.print(i+" ");
            }
            System.out.println("");
        }
        
        
    }
}
